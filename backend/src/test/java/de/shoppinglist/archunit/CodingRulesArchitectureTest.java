package de.shoppinglist.archunit;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;

import java.util.List;

import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@AnalyzeClasses(packages = "de.shoppinglist")
public class CodingRulesArchitectureTest {

    /**
     * A rule that checks that none of the given classes access the standard streams System.out and System.err.
     */
    @ArchTest
    private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    /**
     * A rule that checks that none of the given classes throw generic exceptions.
     */
    @ArchTest
    private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    /**
     * A rule that checks that none of the given classes access Java Util Logging.
     */
    @ArchTest
    private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    /**
     * A rule that checks that none of the given classes use field injection.
     */
    @ArchTest
    private final ArchRule no_field_injection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    /**
     * A rule that checks that all loggers are private, static and final.
     */
    @ArchTest
    private final ArchRule loggers_should_be_private_static_final = fields().that()
            .haveRawType(Logger.class)
            .should().bePrivate()
            .andShould().beStatic()
            .andShould().beFinal()
            .because("loggers should be private, static and final.");

    /**
     * A rule that checks that the logging Method of the LoggingAspect logs.
     * In other Architectures this could be used to check if all Controller-Methods log something.
     */
    @ArchTest
    private final ArchRule aspect_should_log = methods()
            .that().areAnnotatedWith(Around.class)
            .and().areDeclaredInClassesThat().areAnnotatedWith(Aspect.class)
            .and().haveNameContaining("log")
            .should(log());

    /**
     * A ArchCondition that checks if a method logs something.
     *
     * @return ArchCondition
     */
    private static ArchCondition<? super JavaMethod> log() {
        return new ArchCondition<>("a method should log something") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents events) {
                List<JavaMethodCall> loggingCalls = javaMethod.getMethodCallsFromSelf().stream()
                        .filter(call -> call.getTargetOwner().isEquivalentTo(Logger.class))
                        .toList();

                if (loggingCalls.isEmpty()) {
                    events.add(violated(javaMethod, createMessage(javaMethod, "Method does not log")));
                } else {
                    loggingCalls.forEach(loggingCall -> events.add(satisfied(javaMethod, createMessage(javaMethod, loggingCall.getDescription()))));

                }
            }
        };
    }

    /**
     * A rule that checks that no method calls deprecated methods.
     */
    @ArchTest
    private final ArchRule methods_should_not_call_deprecated_methods = methods().should(notCallDeprecatedMethods());

    /**
     * A ArchCondition that checks if a method calls deprecated methods.
     *
     * @return ArchCondition
     */
    private static ArchCondition<? super JavaMethod> notCallDeprecatedMethods() {
        return new ArchCondition<>("a method should not call deprecated methods") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents events) {
                if (javaMethod.getMethodCallsFromSelf().stream().noneMatch(javaMethodCall -> javaMethodCall.getTarget().isAnnotatedWith(Deprecated.class))) {
                    events.add(satisfied(javaMethod, createMessage(javaMethod, "Method does not call deprecated methods")));
                } else {
                    events.add(violated(javaMethod, createMessage(javaMethod, "Method calls deprecated methods")));
                }
            }
        };
    }
}
