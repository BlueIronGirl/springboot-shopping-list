package de.shoppinglist.archunit;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.tngtech.archunit.lang.ConditionEvent.createMessage;
import static com.tngtech.archunit.lang.SimpleConditionEvent.satisfied;
import static com.tngtech.archunit.lang.SimpleConditionEvent.violated;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "de.shoppinglist")
public class UnitArchitectureTest {

    /**
     * A rule that checks that all unit tests contain an assertion.
     */
    @ArchTest
    private final ArchRule unit_tests_should_have_an_assertion_or_an_verification = methods()
            .that().areAnnotatedWith(Test.class)
            .should(callAnAssertion());

    private static ArchCondition<? super JavaMethod> callAnAssertion() {
        return new ArchCondition<>("a unit test should assert something") {
            @Override
            public void check(JavaMethod javaMethod, ConditionEvents events) {
                if (javaMethod.getMethodCallsFromSelf().stream().noneMatch(javaMethodCall -> javaMethodCall.getTargetOwner().isAssignableTo(Assertions.class)) &&
                        javaMethod.getMethodCallsFromSelf().stream().noneMatch(javaMethodCall -> javaMethodCall.getName().startsWith("verify"))) {
                    events.add(violated(javaMethod, createMessage(javaMethod, "Method does not contain an assertion or verify")));
                } else {
                    events.add(satisfied(javaMethod, "Method contains an assertion"));
                }
            }
        };
    }
}
