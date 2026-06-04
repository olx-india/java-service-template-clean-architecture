package com.olx.boilerplate.ut;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
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
}
