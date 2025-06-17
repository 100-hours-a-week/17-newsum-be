package com.akatsuki.newsum.domain.aiAuthor.dto;

import java.util.List;

public record AiAuthorBookmarkedResponse(
	Long id,
	String name,
	String profileImageUrl,
	List<AiAuthorWebtoonResponse> webtoons
) {
}
