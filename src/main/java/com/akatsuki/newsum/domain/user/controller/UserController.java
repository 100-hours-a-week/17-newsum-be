package com.akatsuki.newsum.domain.user.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akatsuki.newsum.common.dto.ApiResponse;
import com.akatsuki.newsum.common.dto.ResponseCodeAndMessage;
import com.akatsuki.newsum.common.pagination.CursorPaginationService;
import com.akatsuki.newsum.common.pagination.annotation.CursorParam;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.page.CursorPage;
import com.akatsuki.newsum.common.security.UserDetailsImpl;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorBookmarkedResponse;
import com.akatsuki.newsum.domain.aiAuthor.service.AiAuthorService;
import com.akatsuki.newsum.domain.user.dto.KeywordListResponse;
import com.akatsuki.newsum.domain.user.dto.KeywordSubscriptionRequest;
import com.akatsuki.newsum.domain.user.dto.RecentViewWebtoonListResponse;
import com.akatsuki.newsum.domain.user.dto.UpdateUserRequestDto;
import com.akatsuki.newsum.domain.user.dto.UpdateUserResponseDto;
import com.akatsuki.newsum.domain.user.dto.UserFavoriteWebtoonsResponse;
import com.akatsuki.newsum.domain.user.dto.UserProfileDto;
import com.akatsuki.newsum.domain.user.service.KeywordService;
import com.akatsuki.newsum.domain.user.service.UserService;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonCardDto;
import com.akatsuki.newsum.domain.webtoon.service.WebtoonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final WebtoonService webtoonService;
	private final KeywordService keywordService;
	private final CursorPaginationService cursorPaginationService;
	private final AiAuthorService aiAuthorService;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		var user = userDetails.getUser();

		UserProfileDto dto = new UserProfileDto(
			user.getEmail(),
			user.getNickname(),
			user.getProfileImageUrl(),
			String.valueOf(user.getId())
		);

		return ResponseEntity.ok(ApiResponse.success(ResponseCodeAndMessage.USER_INFO_SUCCESS, dto));
	}

	@GetMapping("/webtoons/recent")
	public ResponseEntity<ApiResponse<RecentViewWebtoonListResponse>> getRecentWebtoonList(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long id = getUserId(userDetails);
		Map<String, List<WebtoonCardDto>> result = userService.findRecentWebtoonList(id);
		RecentViewWebtoonListResponse response = new RecentViewWebtoonListResponse(result);
		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.USER_RECENTLY_VIEWED_WEBTOON_LIST_SUCCESS, response));
	}

	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<UpdateUserResponseDto>> updateMyProfile(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody UpdateUserRequestDto dto
	) {
		Long userId = getUserId(userDetails);
		UpdateUserResponseDto responseDto = userService.updateUser(userId, dto);

		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.USER_INFO_UPDATE_SUCCESS, responseDto)
		);
	}

	@GetMapping("/favorites/webtoons")
	public ResponseEntity<ApiResponse<UserFavoriteWebtoonsResponse>> getBookmarkedWebtoons(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@CursorParam Cursor cursor,
		@RequestParam(defaultValue = "10") int size
	) {
		Long userId = getUserId(userDetails);

		CursorPage<WebtoonCardDto> page = webtoonService.getBookmarkedWebtoonCards(userId, cursor,
			size);

		UserFavoriteWebtoonsResponse response = new UserFavoriteWebtoonsResponse(
			page.getItems(),
			page.getPageInfo()
		);

		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.WEBTOON_BOOKMARK_SUCCESS, response)
		);

	}

	@GetMapping("/favorite/ai-authors")
	public ResponseEntity<ApiResponse<List<AiAuthorBookmarkedResponse>>> getBookmarkedAiAuthors(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = getUserId(userDetails);
		List<AiAuthorBookmarkedResponse> result = aiAuthorService.getBookmarkedAuthor(userId);

		return ResponseEntity.ok(ApiResponse.success(ResponseCodeAndMessage.AI_AUTHOR_FAVORITE_LIST_SUCCESS, result)
		);
	}

	@PostMapping("/keywords/subscriptions")
	public ResponseEntity<ApiResponse> addKeyword(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody @Valid KeywordSubscriptionRequest request
	) {
		Long userId = getUserId(userDetails);
		keywordService.subscribeKeyword(userId, request.keyword());

		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.KEYWORD_SUBSCRIBE_SUCCESS, null)
		);
	}

	@DeleteMapping("/keywords/{keywordId}")
	public ResponseEntity<ApiResponse> deleteKeyword(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long keywordId
	) {
		Long userId = getUserId(userDetails);
		keywordService.unsubscribeKeyword(userId, keywordId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/keywords")
	public ResponseEntity<ApiResponse<KeywordListResponse>> getKeywordList(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		Long userId = getUserId(userDetails);
		KeywordListResponse result = keywordService.getKeywordList(userId);

		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.KEYWORD_LIST_SUCCESS, result)
		);
	}

	@GetMapping("/keywords/webtoons")
	public ResponseEntity<ApiResponse<UserFavoriteWebtoonsResponse>> getWebtoonsByKeywordBookmarks(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@CursorParam Cursor cursor,
		@RequestParam(defaultValue = "10") int size
	) {
		Long userId = getUserId(userDetails);

		List<WebtoonCardDto> webtoons = webtoonService.findWebtoonsByUserKeywords(userId, cursor, size);
		CursorPage<WebtoonCardDto> page = cursorPaginationService.create(webtoons, size, cursor);

		UserFavoriteWebtoonsResponse response = new UserFavoriteWebtoonsResponse(
			page.getItems(),
			page.getPageInfo()
		);

		return ResponseEntity.ok(
			ApiResponse.success(ResponseCodeAndMessage.WEBTOON_LIST_SUCCESS, response)
		);
	}

	private Long getUserId(
		UserDetailsImpl userDetails) {
		if (userDetails == null) {
			return null;
		}
		return userDetails.getUserId();
	}

}
