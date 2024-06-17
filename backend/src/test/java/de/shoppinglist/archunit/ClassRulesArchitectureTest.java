package de.shoppinglist.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import de.shoppinglist.controller.AuthController;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

@AnalyzeClasses(packages = "de.shoppinglist")
public class ClassRulesArchitectureTest {
    /**
     * A rule that checks that all controllers are in a controller package, are annotated with Controller or RestController and have Controller at the end of the class name.
     */
    @ArchTest
    private final ArchRule controller_should_be_annotated_with_controller_annotation_and_should_have_a_method_security_check_and_have_a_name_end_with_controller_and_are_inside_controller_package = classes()
            .that().areAnnotatedWith(Controller.class)
            .or().areAnnotatedWith(RestController.class)
            .or().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..controller..")
            .andShould().beAnnotatedWith(Controller.class).orShould().beAnnotatedWith(RestController.class)
            .andShould().beAnnotatedWith(PreAuthorize.class).orShould().be(AuthController.class)
            .andShould().haveSimpleNameEndingWith("Controller")
            .because("controllers should be in a controller package, be annotated with Controller or RestController and have Controller at the end of the class name.");

    /**
     * A rule that checks that all controllers methods only return an dto
     */
    @ArchTest
    private final ArchRule controller_methods_should_return_an_dto_or_void = methods()
            .that().areAnnotatedWith(GetMapping.class)
            .or().areAnnotatedWith(PostMapping.class)
            .or().areAnnotatedWith(PutMapping.class)
            .or().areAnnotatedWith(DeleteMapping.class)

            .should(returnAnDTO())
            .because("controllers should return an responseentity with an dto or void");


    private static ArchCondition<? super JavaMethod> returnAnDTO() {
        return new ArchCondition<>("a controller method should return an dto and not an entity") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents events) {
                List<JavaClass> allClasses = javaMethod.getReturnType().getAllInvolvedRawTypes().stream().toList();

                if (allClasses.stream().noneMatch(clazz -> clazz.getFullName().equals(ResponseEntity.class.getName()))) {
                    events.add(violated(javaMethod, createMessage(javaMethod, "Method does not return ResponseEntity")));
                } else if (allClasses.stream().noneMatch(clazz -> clazz.getFullName().endsWith("DTO") || clazz.getFullName().endsWith(String.class.getName()) || clazz.getFullName().endsWith(Void.class.getName()))) {
                    events.add(violated(javaMethod, createMessage(javaMethod, "Method does not return DTO")));
                }
            }
        }

                ;
    }

    /**
     * A rule that checks that all services are in a service package, are annotated with Service and have Service at the end of the class name.
     */
    @ArchTest
    private final ArchRule service_should_be_annotated_and_have_a_name_end_with_service_and_are_inside_service_package = classes()
            .that().areAnnotatedWith(Service.class)
            .or().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..service..")
            .andShould().beAnnotatedWith(Service.class)
            .andShould().haveSimpleNameEndingWith("Service")
            .because("services should be in a service package, be annotated with Service and have Service at the end of the class name.");

    /**
     * A rule that checks that all repositories are in a repository package, extend JpaRepository and have Repository at the end of the class name.
     */
    @ArchTest
    private final ArchRule repository_should_extend_jparepository_and_have_a_name_end_with_repository_and_are_inside_repository_package = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().resideInAPackage("..repository..")
            .andShould().beAssignableTo(JpaRepository.class)
            .andShould().haveSimpleNameEndingWith("Repository")
            .because("repositories should be in a repository package, extend JpaRepository and have Repository at the end of the class name.");

    /**
     * A rule that checks that all entities are in a entity package and are annotated with Entity.
     */
    @ArchTest
    private final ArchRule entities_should_be_annotated_and_are_inside_entity_package = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("..entity..")
            .andShould().beAnnotatedWith(Entity.class)
            .because("entities should be in a entity package and be annotated with Entity.");

    /**
     * A rule that checks that no class that are outside of the config package depend on classes that are inside the config package.
     */
    @ArchTest
    private final ArchRule no_classes_should_depend_on_config_classes = noClasses()
            .that().resideOutsideOfPackage("de..config")
            .should().dependOnClassesThat().resideInAPackage("de..config..");

    /**
     * A rule that checks that no class depends on upper packages
     */
    @ArchTest
    private final ArchRule no_classes_should_depend_upper_packages = NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

    /**
     * A rule that checks that no class depends on deprecated classes
     */
    @ArchTest
    private final ArchRule no_classes_should_depend_on_deprecated_classes = noClasses().should().dependOnClassesThat().areAnnotatedWith(Deprecated.class);


}
