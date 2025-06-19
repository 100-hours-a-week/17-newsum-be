package com.akatsuki.newsum.common.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * API 에러 코드와 메시지를 정의하는 열거형
 */
@Getter
public enum ErrorCodeAndMessage {

	// 클라이언트 오류 (4xx)
	BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND.value(), "요청한 리소스를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 HTTP 메소드입니다."),
	CONFLICT(HttpStatus.CONFLICT.value(), "리소스 상태가 충돌합니다."),
	PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED.value(), "사전 조건이 충족되지 않았습니다."),
	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY.value(), "요청은 유효하나 처리할 수 없습니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST.value(), "잘못된 입력값입니다."),
	INVALID_CATEGORY(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 카테고리입니다."), // ✅ 추가된 부분

	// 커서, 페이지 네이션 관련 오류
	CURSOR_WRONG_EXPRESSION(HttpStatus.BAD_REQUEST.value(), "지원하지 않는 커서 형식입니다."),

	// 웹툰 관련 클라이언트 오류
	WEBTOON_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 웹툰입니다."),
	INVALID_WEBTOON_ID(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 웹툰 ID입니다."),
	INVALID_WEBTOON_IMAGE_URL(HttpStatus.BAD_REQUEST.value(), "잘못된 이미지 생성 요청입니다."),
	DUPLICATE_WEBTOON_LIKE(HttpStatus.CONFLICT.value(), "이미 좋아요한 웹툰입니다."),
	DUPLICATE_WEBTOON_BOOKMARK(HttpStatus.CONFLICT.value(), "이미 북마크한 웹툰입니다."),

	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "댓글을 찾을 수 없습니다."),
	COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "댓글에 대한 접근 권한이 없습니다."),

	AI_AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "AI작가를 찾을 수 없습니다."),

	// 사용자 관련 클라이언트 오류
	USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 사용자입니다."),
	INVALID_USER_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 사용자 인증정보입니다."),
	DUPLICATE_NICKNAME_GENERATION_FAILED(409, "중복되지 않는 닉네임을 찾을 수 없습니다."),

	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "알림을 찾을 수 없습니다"),

	// SSE 관련 오류
	SSE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SSE연결 객체를 찾을 수 없습니다."),

	// 서버 오류 (5xx)
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."),
	NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED.value(), "아직 구현되지 않은 기능입니다."),
	SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "서비스를 일시적으로 사용할 수 없습니다."),

	// 구독 관련 오류
	KEYWORD_ALREADY_SUBSCRIBED(HttpStatus.CONFLICT.value(), "이미 구독한 키워드입니다."),
	KEYWORD_SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 키워드입니다."),

	EXTERN_SERVER_HEALTH_CHECK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "외부 서버가 연결되지 않습니다."),
	EXTERN_SERVER_UNAVAILABLE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "외부 서버의 접근이 불가합니다."),
	// 이미지 생성 큐 관련 오류
	IMAGE_GENERATION_QUEUE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "존재하지 않는 이미지 생성 요청입니다."),
	;

	private final int code;
	private final String message;

	ErrorCodeAndMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
