package com.akatsuki.newsum.domain.webtoon.entity.webtoon;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;

public enum Category {
	IT, FINANCE, POLITICS;

	public static Category from(String category) {
		if (category == null || category.trim().isEmpty()) {
			throw new BusinessException(ErrorCodeAndMessage.INVALID_CATEGORY);
		}
		try {
			return Category.valueOf(category.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCodeAndMessage.INVALID_CATEGORY);
		}
	}
}
