package eu.btimpl.testing.controller;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "eu.btimpl.testing",
importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

  @ArchTest
  static final ArchRule layered_architecture_should_be_respected = layeredArchitecture()
      .consideringAllDependencies()
      .layer("Controller").definedBy("..controller..")
      .layer("Service").definedBy("..service..")
      .layer("Repository").definedBy("..repository..")

      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

  @ArchTest
  static final ArchRule controllers_should_not_depend_on_repositories = noClasses()
      .that().resideInAPackage("..controller..")
      .should().dependOnClassesThat().resideInAPackage("..repository..");

  @ArchTest
  static final ArchRule service_field_naming_and_annotation = classes()
      .that().areAnnotatedWith(Service.class)
      .should().haveSimpleNameEndingWith("Service");

  @ArchTest
  static final ArchRule controller_naming_and_annotation = classes()
      .that().areAnnotatedWith(RestController.class)
      .should().haveSimpleNameEndingWith("Controller");

  @ArchTest
  static final ArchRule no_cycles_between_packages = slices()
      .matching("eu.btimpl.testing.(*)..") // Every subfolder is a slice
      .should().beFreeOfCycles();

  @ArchTest
  static final ArchRule transactional_classes_should_reside_in_service_package = classes()
      .that().areAnnotatedWith(Transactional.class)
      .should().resideInAPackage("..service..")
      .allowEmptyShould(true); //If it is a rule which is not used, for ex. this app doesn't contains any @Transactional annotation

  @ArchTest
  static final ArchRule entities_must_have_no_public_fields = classes()
      .that().areAnnotatedWith(Entity.class)
      .should(haveNoPublicFields());

  private static ArchCondition<JavaClass> haveNoPublicFields() {
    return new ArchCondition<>("have no public fields") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        javaClass.getFields().stream()
            .filter(field -> field.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.PUBLIC))
            .forEach(field -> {
              String message = String.format("Field %s in class %s is public!", field.getName(), javaClass.getName());
              events.add(SimpleConditionEvent.violated(field, message));
            });
      }
    };
  }
}
