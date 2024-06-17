package de.shoppinglist.aspect;

import de.shoppinglist.entity.Einkaufszettel;
import de.shoppinglist.entity.User;
import de.shoppinglist.exception.UnautorizedException;
import de.shoppinglist.repository.EinkaufszettelRepository;
import de.shoppinglist.service.UserAuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Security-Aspect for the application to check all Method-Call-Parameters and return-values if the user has access to the objects
 */
@Aspect
@Component
public class SecurityAspect {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    private final UserAuthenticationService userAuthenticationService;
    private final EinkaufszettelRepository einkaufszettelRepository;

    public SecurityAspect(UserAuthenticationService userAuthenticationService, EinkaufszettelRepository einkaufszettelRepository) {
        this.userAuthenticationService = userAuthenticationService;
        this.einkaufszettelRepository = einkaufszettelRepository;
    }

    /**
     * Pointcut that matches all Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(de.shoppinglist..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }


    /**
     * Advice that logs all Method-Call-Parameters and Return-Values
     *
     * @param joinPoint Specific Joinpoint of the Method
     * @return Result of the called Method
     * @throws Throwable Exception that is thrown by the called Method
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object checkAround(ProceedingJoinPoint joinPoint) throws Throwable {
        User currentUser = this.userAuthenticationService.findCurrentUser();

        if (!joinPoint.getSignature().getName().startsWith("create")) {
            for (Object arg : joinPoint.getArgs()) {
                validateObject(arg, currentUser);
            }
        }

        Object result = joinPoint.proceed(); // call original Method

        validateObject(result, currentUser); // result nochmal validieren

        return result; // return result of the original Method
    }

    private void validateObject(Object arg, User currentUser) throws IllegalAccessException {
        Einkaufszettel einkaufszettel = null;

        if (arg instanceof ResponseEntity<?> responseEntity && responseEntity.getBody() != null) {
            arg = responseEntity.getBody();
        }

        // bei Array rekursiv mit den einzelnen Objekten nochmal die Methode aufrufen
        if (arg.getClass().isArray()) {
            Object[] objects = (Object[]) arg;
            for (Object object : objects) {
                validateObject(object, currentUser);
            }
            return;
        } else if (arg instanceof List<?> list) {
            for (Object object : list) {
                validateObject(object, currentUser);
            }
            return;
        }


        if (arg instanceof Einkaufszettel argEinkaufszettel) {
            einkaufszettel = argEinkaufszettel;
        } else {
            Field fieldEinkaufszettel = ReflectionUtils.findField(arg.getClass(), null, Einkaufszettel.class);
            if (fieldEinkaufszettel != null) {
                fieldEinkaufszettel.setAccessible(true);
                einkaufszettel = (Einkaufszettel) fieldEinkaufszettel.get(arg);
            }
        }

        if (einkaufszettel != null) {
            Einkaufszettel einkaufszettelDB = einkaufszettelRepository.findByIdAndOwners_IdOrSharedWith_Id(einkaufszettel.getId(), currentUser, currentUser)
                    .orElseThrow(() -> new UnautorizedException("Sie haben keinen Zugriff auf das Objekt"));
            if (!(einkaufszettelDB.getOwners().stream().anyMatch(currentUser::equals) || einkaufszettelDB.getSharedWith().stream().anyMatch(currentUser::equals))) {
                throw new UnautorizedException("Sie haben keinen Zugriff auf das Objekt");
            }
        }
    }
}
