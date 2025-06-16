package com.akatsuki.newsum.domain.aiAuthor.dto;

import java.util.List;

public record AiAuthorDetailResponse(
	Long id,
	String name,
	String style,
	String introduction,
	String profileImageUrl,
	List<AiAuthorWebtoonResponse> webtoons,
	boolean isSubscribed
) {
}
