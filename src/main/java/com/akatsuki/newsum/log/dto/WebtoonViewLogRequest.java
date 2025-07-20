package com.akatsuki.newsum.domain.log.dto;

public record WebtoonViewLogRequest(
	String userId,
	Long webtoonId
) {
}
