package com.akatsuki.newsum.domain.user.dto;

import java.time.LocalDateTime;

public record KeywordResponse(
	Long id,
	String content,
	LocalDateTime createAt
) {
	public static KeywordResponse of(Long id, String content, LocalDateTime createAt) {
		return new KeywordResponse(id, content, createAt);
	}
}
