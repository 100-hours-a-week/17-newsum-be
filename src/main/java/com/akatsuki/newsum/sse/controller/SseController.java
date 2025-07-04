package com.akatsuki.newsum.sse.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.common.security.TokenProvider;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;
import com.akatsuki.newsum.sse.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/v1/sse")
@RestController
@RequiredArgsConstructor
public class SseController {

	private final SseService sseService;
	private final TokenProvider tokenProvider;

	@GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> sse(
		@RequestParam(value = "clientId", required = false) String clientId,
		@RequestParam(value = "accessToken") String token
	) {
		if (token != null) {
			String userId = getUserId(token);
			SseEmitter response = sseService.subscribe(String.valueOf(userId), clientId);
			return ResponseEntity.ok(response);
		} else {
			SseEmitter response = sseService.subscribe(clientId);
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping(value = "/webtoon/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> subscribeToWebtoon(
		@RequestParam("webtoonId") Long webtoonId,
		@RequestParam("clientId") String clientId
	) {
		SseEmitter response = sseService.startViewingWebtoon(webtoonId, clientId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/webtoon/leave")
	public ResponseEntity<Void> leaveWebtoon(
		@RequestBody WebtoonViewerEvent request
	) {
		sseService.leaveWebtoon(request.webtoonId(), request.clientId());
		return ResponseEntity.ok().build();
	}

	private String getUserId(String token) {
		tokenProvider.validateToken(token);
		return String.valueOf(tokenProvider.getUserIdFromToken(token));
	}
}
