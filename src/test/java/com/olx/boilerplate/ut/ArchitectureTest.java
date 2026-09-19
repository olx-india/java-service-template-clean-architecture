package com.olx.boilerplate.ut;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.olx.boilerplate");

    @Test
    void domainShouldNotDependOnOuterLayers() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("..domain..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                                                           "..controller..", "..infrastructure..", "..usecase..");
        rule.check(CLASSES);
    }

    @Test
    void usecaseShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("..usecase..")
                        .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..");
        rule.check(CLASSES);
    }

    @Test
    void usecaseShouldNotDependOnController() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("..usecase..")
                        .should().dependOnClassesThat().resideInAPackage("..controller..");
        rule.check(CLASSES);
    }

    @Test
    void controllersShouldNotDependOnInfrastructureExceptLocalDemo() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("..controller..")
                        .and().doNotHaveSimpleName("ClientController")
                        .should().dependOnClassesThat().resideInAPackage("..infrastructure..");
        rule.check(CLASSES);
    }

    @Test
    void domainShouldNotDependOnSpringDataOrJpaOrKafka() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("..domain..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                                                           "org.springframework.data..",
                                                                           "org.springframework.kafka..",
                                                                           "jakarta.persistence..",
                                                                           "javax.persistence..");
        rule.check(CLASSES);
    }

    @Test
    void usercaseTypoPackageMustNotExist() {
        boolean hasUsercase = CLASSES.stream().anyMatch(javaClass -> javaClass.getPackageName().contains(".usercase"));
        assertTrue(!hasUsercase, "Package 'usercase' must not exist; use 'usecase'");
    }

    @Test
    void slf4jMustStayInLoggingAdapter() {
        ArchRule rule = noClasses()
                        .that().resideInAPackage("com.olx.boilerplate..")
                        .and().resideOutsideOfPackage("..infrastructure.logging..")
                        .should().dependOnClassesThat().resideInAnyPackage("org.slf4j..");
        rule.check(CLASSES);
    }
}
