package com.akatsuki.newsum.domain.webtoon.dto;

import java.time.LocalDateTime;

public record WebtoonCardDto(
	Long id,
	String title,
	String thumbnailUrl,
	LocalDateTime createdAt,
	Long viewCount
) {
}
