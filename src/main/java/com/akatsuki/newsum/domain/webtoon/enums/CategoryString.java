package com.akatsuki.newsum.domain.webtoon.enums;

import com.akatsuki.newsum.common.enums.EnumString;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.fasterxml.jackson.annotation.JsonCreator;

public class CategoryString extends EnumString<Category> {

	// 부모에게 value를 넘김
	public CategoryString(String value) {
		super(value);
	}

	// 역직렬화 지원
	@JsonCreator
	public static CategoryString from(String value) {
		return new CategoryString(value);
	}
}
