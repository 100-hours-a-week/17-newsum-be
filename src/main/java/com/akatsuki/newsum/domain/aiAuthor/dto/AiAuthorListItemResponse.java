package com.akatsuki.newsum.domain.aiAuthor.dto;

public record AiAuthorListItemResponse(
	Long id,
	String name,
	String profileImageUrl,
	boolean subscribed
) {
}
