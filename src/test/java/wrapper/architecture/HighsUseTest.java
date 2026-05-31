package wrapper.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import highs.Highs;
import org.junit.jupiter.api.Test;
import wrapper.model.Model;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HighsUseTest {

    @Test
    void model_should_be_the_only_class_depending_on_highs() {
        final JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(List.of("..wrapper.."));

        final DescribedPredicate<JavaClass> dependsOnHighsPredicates = new DescribedPredicate<>("depend on Highs") {
            @Override
            public boolean test(final JavaClass javaClass) {
                return javaClass.getDirectDependenciesFromSelf().stream()
                        .anyMatch(dep -> dep.getTargetClass().isAssignableTo(Highs.class));
            }
        };

        assertFalse(importedClasses.isEmpty());
        final ArchRule rule = classes()
                .that(dependsOnHighsPredicates)
                .should().be(Model.class);
        rule.check(importedClasses);
    }

}
