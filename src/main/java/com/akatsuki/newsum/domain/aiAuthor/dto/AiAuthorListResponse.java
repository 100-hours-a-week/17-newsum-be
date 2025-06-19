package com.akatsuki.newsum.domain.aiAuthor.dto;

import java.util.List;

public record AiAuthorListResponse(
	List<AiAuthorListItemResponse> authors
) {
}
