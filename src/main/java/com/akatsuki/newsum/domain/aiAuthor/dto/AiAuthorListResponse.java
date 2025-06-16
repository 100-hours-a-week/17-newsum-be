package com.akatsuki.newsum.domain.aiAuthor.dto;

import java.util.List;

import com.akatsuki.newsum.common.pagination.model.page.PageInfo;

public record AiAuthorListResponse(
	List<AiAuthorListItemResponse> authors,
	PageInfo pageInfo
) {
}
