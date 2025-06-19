package com.akatsuki.newsum.domain.webtoon.dto;

import java.util.List;

public record WebtoonTopResponse(
	List<WebtoonCardDto> top3News,
	List<WebtoonCardDto> todayNews,
	Boolean hasNewNotification
) {
}
