package com.akatsuki.newsum.domain.webtoon.dto;

import java.util.List;

public record TodayWebtoonsResponse(
	List<WebtoonCardDto> TodayWebtoons
) {
}
