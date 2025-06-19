package com.akatsuki.newsum.common.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * API 성공 응답 코드와 메시지를 정의하는 열거형
 */
@Getter
public enum ResponseCodeAndMessage {
	// 성공 응답 (2xx)
	SUCCESS(HttpStatus.OK.value(), "요청이 성공적으로 처리되었습니다."),
	CREATED(HttpStatus.CREATED.value(), "리소스가 성공적으로 생성되었습니다."),
	NO_CONTENT(HttpStatus.NO_CONTENT.value(), "성공적으로 처리되었으며 반환할 콘텐츠가 없습니다."),

	// 웹툰 관련 성공 응답
	WEBTOON_BASE_INFO_SUCCESS(HttpStatus.OK.value(), "웹툰 기본 정보 조회에 성공했습니다."),
	WEBTOON_DETAIL_SUCCESS(HttpStatus.OK.value(), "웹툰 상세 정보 조회에 성공했습니다."),
	WEBTOON_LIST_SUCCESS(HttpStatus.OK.value(), "웹툰 목록 조회에 성공했습니다."),
	WEBTOON_LIKE_SUCCESS(HttpStatus.OK.value(), "웹툰 좋아요가 성공적으로 처리되었습니다."),
	WEBTOON_BOOKMARK_SUCCESS(HttpStatus.OK.value(), "웹툰 북마크가 성공적으로 처리되었습니다."),
	WEBTOON_TOP_SUCCESS(HttpStatus.OK.value(), "홈 화면용 데이터 조회에 성공했습니다."),
	WEBTOON_CREATE_SUCCESS(HttpStatus.CREATED.value(), "웹툰 생성에 성공했습니다."),
	WEBTOON_MAIN_SUCCESS(HttpStatus.OK.value(), "카테고리별 웹툰 목록 조회에 성공했습니다."),
	WEBTOON_SEARCH_SUCCESS(HttpStatus.OK.value(), "웹툰 검색에 성공했습니다"),

	// 사용자 관련 성공 응답
	USER_REGISTER_SUCCESS(HttpStatus.CREATED.value(), "회원가입이 성공적으로 완료되었습니다."),
	USER_LOGIN_SUCCESS(HttpStatus.OK.value(), "로그인이 성공적으로 완료되었습니다."),
	USER_INFO_SUCCESS(HttpStatus.OK.value(), "사용자 정보 조회에 성공했습니다."),
	USER_RECENTLY_VIEWED_WEBTOON_LIST_SUCCESS(HttpStatus.OK.value(), "최근 본 웹툰 목록 조회에 성공했습니다."),
	USER_INFO_UPDATE_SUCCESS(HttpStatus.OK.value(), "프로필 수정이 완료되었습니다."),

	COMMENT_FIND_SUCCESS(HttpStatus.OK.value(), "댓글 목록 조회에 성공했습니다."),
	COMMENT_DELETE_SUCCESS(HttpStatus.NO_CONTENT.value(), "댓글 삭제에 성공했습니다."),
	COMMEND_EDIT_SUCCESS(HttpStatus.OK.value(), "댓글 수정에 성공했습니다."),
	COMMENT_ADD_SUCCESS(HttpStatus.CREATED.value(), "댓글 생성에 성공했습니다."),

	//좋아요 관련 성공 응답
	ARTICLE_LIKE_CHECK_SUCCESS(HttpStatus.OK.value(), "웹툰 좋아요 여부 조회에 성공했습니다."),
	ARTICLE_LIKE_TOGGLE_SUCCESS(HttpStatus.OK.value(), "웹툰 좋아요 요청에 성공했습니다."),
	ARTICLE_LIKE_COUNT_SUCCESS(HttpStatus.OK.value(), "웹툰 좋아요 수 조회에 성공했습니다."),

	//알림
	NOTI_LIST_SUCCESS(HttpStatus.OK.value(), "알림 목록 조회에 성공했습니다."),
	NOTI_READ_SUCCESS(HttpStatus.OK.value(), "알람 읽음 처리에 성공했습니다."),

	COMMENT_LIKE_CHECK_SUCCESS(HttpStatus.OK.value(), "댓글 좋아요 여부 조회에 성공했습니다."),
	COMMENT_LIKE_TOGGLE_SUCCESS(HttpStatus.OK.value(), "댓글 좋아요 요청에 성공했습니다."),

	//키워드 성공 응답
	KEYWORD_SUBSCRIBE_SUCCESS(HttpStatus.OK.value(), "키워드 구독에 성공했습니다."),
	WEBTOON_KEYWORD_SEARCH_SUCCESS(HttpStatus.OK.value(), "키워드 기반 웹툰 목록 조회에 성공했습니다."),
	KEYWORD_LIST_SUCCESS(HttpStatus.OK.value(), "키워드 목록 조회에 성공했습니다"),

	//AI서버 성공 응답
	AI_IMAGE_PROMPT_SAVED_SUCCESS(HttpStatus.OK.value(), "이미지 생성 큐 저장에 성공했습니다."),
	AI_WEBTOON_CREATED_SUCCESSFULLY(HttpStatus.OK.value(), "AI 이미지 결과를 기반으로 웹툰이 성공적으로 저장되었습니다"),

	//AI작가 북마크 관련 성공 응답
	AI_AUTHOR_SUBSCRIBE_TOGGLE_SUCCESS(HttpStatus.OK.value(), "AI작가 구독 상태가 변경되었습니다."),
	AI_AUTHOR_DETAIL_SUCCESS(HttpStatus.OK.value(), "AI작가 상세 조회에 성공했습니다."),
	AI_AUTHOR_LIST_SUCCESS(HttpStatus.OK.value(), "AI작가 목록 조회에 성공했습니다."),
	AI_AUTHOR_FAVORITE_LIST_SUCCESS(HttpStatus.OK.value(), "AI작가 북마크 목록 조회에 성공했습니다.");

	private final int code;
	private final String message;

	ResponseCodeAndMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
