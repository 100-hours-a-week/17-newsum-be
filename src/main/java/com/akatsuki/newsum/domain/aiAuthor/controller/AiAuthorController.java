package com.akatsuki.newsum.domain.aiAuthor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akatsuki.newsum.common.dto.ApiResponse;
import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.dto.ResponseCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.pagination.annotation.CursorParam;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.security.UserDetailsImpl;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorDetailResponse;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorListResponse;
import com.akatsuki.newsum.domain.aiAuthor.service.AiAuthorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ai-authors")
public class AiAuthorController {

	private final AiAuthorService aiAuthorService;

	@PostMapping("/{aiAuthorId}/subscriptions")
	public ResponseEntity<ApiResponse> subscribe(
		@PathVariable Long aiAuthorId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = getUserId(userDetails);
		aiAuthorService.toggleSubscribe(userId, aiAuthorId);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_SUBSCRIBE_TOGGLE_SUCCESS, null)
		);
	}

	//작가 상세페이지
	@GetMapping("{aiAuthorId}")
	public ResponseEntity<ApiResponse<AiAuthorDetailResponse>> getAuthorDetail(
		@PathVariable Long aiAuthorId,
		@AuthenticationPrincipal UserDetailsImpl userDetails

	) {
		Long userId = (userDetails != null) ? userDetails.getUserId() : null; //비로그인유저도 접근 가능하도록
		AiAuthorDetailResponse result = aiAuthorService.getAuthorDetail(userId, aiAuthorId);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_DETAIL_SUCCESS, result)
		);
	}

	//작가리스트
	@GetMapping
	public ResponseEntity<ApiResponse<AiAuthorListResponse>> getAiAuthors(
		@CursorParam Cursor cursor,
		@RequestParam(defaultValue = "10", required = false) Integer size,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = (userDetails != null) ? userDetails.getUserId() : null; //비로그인유저도 접근 가능하도록
		AiAuthorListResponse response = aiAuthorService.getAuthorList(userId, cursor, size);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_LIST_SUCCESS, response)
		);
	}

	private Long getUserId(
		UserDetailsImpl userDetails) {
		if (userDetails == null) {
			throw new BusinessException(ErrorCodeAndMessage.UNAUTHORIZED);
		}
		return userDetails.getUserId();
	}
}
