package com.akatsuki.newsum.domain.webtoon.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WebtoonStaticDto(
	Long id,
	String title,
	String thumbnailImageUrl,
	List<WebtoonSlideDto> slides,
	AiAuthorInfoDto authorInfo,
	long viewCount,
	LocalDateTime createdAt
) {
}
