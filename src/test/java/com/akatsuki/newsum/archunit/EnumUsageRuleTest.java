package com.akatsuki.newsum.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import org.junit.jupiter.api.Test;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.akatsuki.newsum")
public class EnumUsageRuleTest {

	@Test
	void dto는_Category_enum_직접_사용하면_안된다() {
		var classes = new ClassFileImporter().importPackages("com.akatsuki.newsum");

		DescribedPredicate<JavaClass> isCategoryEnum = new DescribedPredicate<>("Category enum") {
			@Override
			public boolean test(JavaClass input) {
				return input.getFullName().equals(Category.class.getName());
			}
		};

		ArchRule rule = noClasses()
			.that().resideInAnyPackage("..dto..")
			.and().resideOutsideOfPackage("..common.dto..")
			.should().dependOnClassesThat(isCategoryEnum)
			.because("DTO는 Category enum을 직접 의존하면 안 되고 EnumString으로 감싸야 한다");

		rule.check(classes);
	}
}
