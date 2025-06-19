package com.akatsuki.newsum.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record KeywordSubscriptionRequest(
	@NotBlank(message = "키워드는 필수입니다.")
	String keyword
) {
}
