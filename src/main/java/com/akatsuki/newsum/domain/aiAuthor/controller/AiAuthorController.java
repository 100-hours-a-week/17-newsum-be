package com.akatsuki.newsum.domain.aiAuthor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akatsuki.newsum.common.dto.ApiResponse;
import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.dto.ResponseCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.security.UserDetailsImpl;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorDetailResponse;
import com.akatsuki.newsum.domain.aiAuthor.service.AiAuthorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/ai-authors")
public class AiAuthorController {

	private final AiAuthorService aiAuthorService;

	@PostMapping("/{aiAuthorId}/subscriptions")
	public ResponseEntity<ApiResponse<Void>> subscribe(
		@PathVariable Long aiAuthorId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = getUserId(userDetails);
		aiAuthorService.toggleSubscribe(userId, aiAuthorId);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_TOGGLE_SUCCESS, null)
		);
	}

	@GetMapping("{aiAuthorId}")
	public ResponseEntity<ApiResponse<AiAuthorDetailResponse>> getAuthorDetail(
		@PathVariable Long aiAuthorId
	) {
		AiAuthorDetailResponse result = aiAuthorService.getAuthorDetail(aiAuthorId);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_DETAIL_SUCCESS, result)
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
