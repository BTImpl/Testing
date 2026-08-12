package eu.btimpl.testing.controller;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "eu.btimpl.testing",
importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {
  // 1. SZABÁLY: Rétegzett architektúra (Layered Architecture) szigorú betartása
  @ArchTest
  static final ArchRule layered_architecture_should_be_respected = layeredArchitecture()
      .consideringAllDependencies()
      .layer("Controller").definedBy("..controller..")
      .layer("Service").definedBy("..service..")
      .layer("Repository").definedBy("..repository..")

      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

  // 2. SZABÁLY: A Controller réteg NEM függhet közvetlenül a Repository-tól
  @ArchTest
  static final ArchRule controllers_should_not_depend_on_repositories = noClasses()
      .that().resideInAPackage("..controller..")
      .should().dependOnClassesThat().resideInAPackage("..repository..");

  // 3. SZABÁLY: Névkonvenciók ellenőrzése
  @ArchTest
  static final ArchRule service_field_naming_and_annotation = classes()
      .that().areAnnotatedWith(Service.class)
      .should().haveSimpleNameEndingWith("Service");

  @ArchTest
  static final ArchRule controller_naming_and_annotation = classes()
      .that().areAnnotatedWith(RestController.class)
      .should().haveSimpleNameEndingWith("Controller");
}
