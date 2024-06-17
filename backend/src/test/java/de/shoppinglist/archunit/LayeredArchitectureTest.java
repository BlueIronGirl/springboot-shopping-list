package de.shoppinglist.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameMatching;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "de.shoppinglist")
public class LayeredArchitectureTest {
    /**
     * A rule that checks the Layers of the Architecture:
     * <p>
     * no access to the Configuration Layer
     * no access to the Controller Layer
     * Service Layer may only be accessed by Controller, Service and Configuration Layer
     * Repository Layer may only be accessed by Service Layer
     */
    @ArchTest
    private final ArchRule layeredArchitectureRule = layeredArchitecture().consideringAllDependencies()
            .layer("Configuration").definedBy("..config..")
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .whereLayer("Configuration").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer().ignoreDependency(nameMatching(".+\\.*Aspect"), alwaysTrue()).ignoreDependency(nameMatching(".+\\.*Test"), alwaysTrue())
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service", "Configuration")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service").ignoreDependency(nameMatching(".+\\.*Timer"), alwaysTrue());


}
