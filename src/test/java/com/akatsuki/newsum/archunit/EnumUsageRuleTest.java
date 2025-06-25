package com.akatsuki.newsum.archunit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.akatsuki.newsum")
public class EnumUsageRuleTest {

	@Test
	void dto_enum_직접_사용_금지() {
		JavaClasses classes = new ClassFileImporter().importPackages("com.akatsuki.newsum");
		ArchRule rule = noClasses()
			.that().resideInAnyPackage("..dto..")
			.and().resideOutsideOfPackage("..common.dto..")
			.should()
			.dependOnClassesThat()
			.areAssignableTo(Enum.class)
			.because("DTO는 Enum을 직접 의존하면 안 되고, EnumString 인터페이스를 통해 우회해야 함");
		rule.check(classes);

	}
}
