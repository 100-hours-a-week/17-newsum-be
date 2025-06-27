package com.akatsuki.newsum.sse.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.sse.kafka.WebtoonViewerEventPublisher;
import com.akatsuki.newsum.sse.repository.SseEmitterRepository;
import com.akatsuki.newsum.sse.repository.WebtoonSseEmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

	private final SseEmitterRepository sseEmitterRepository;
	private final WebtoonViewerEventPublisher viewerEventPublisher;
	private final WebtoonSseEmitterRepository webtoonSseEmitterRepository;
	private final Set<String> cleanedUp = ConcurrentHashMap.newKeySet();

	public SseEmitter subscribe(String uuid) {
		SseEmitter emitter = sseEmitterRepository.saveAnonymous(uuid);
		try {
			emitter.send(SseEmitter.event().name("connect").data("SSE Connect Success"));
		} catch (IOException e) {
			emitter.completeWithError(e);
		}
		return emitter;
	}

	public SseEmitter subscribe(String userId, String clientId) {
		SseEmitter emitter = sseEmitterRepository.save(userId, clientId);
		try {
			emitter.send(SseEmitter.event().name("connect").data("SSE Connect Success"));
		} catch (IOException e) {
			emitter.completeWithError(e);
		}
		return emitter;
	}

	public void sendDataToUser(String userId, Object data) {
		sseEmitterRepository.get(userId)
			.forEach(emitter -> {
				try {
					emitter.send(SseEmitter.event().name("data").data(data));
				} catch (Exception e) {
					emitter.completeWithError(e);
					sseEmitterRepository.remove(userId);
				}
			});
	}

	public SseEmitter startViewingWebtoon(Long webtoonId, String clientId) {
		SseEmitter emitter = webtoonSseEmitterRepository.save(webtoonId, clientId);
		viewerEventPublisher.publishJoin(webtoonId, clientId);

		emitter.onCompletion(() -> {
			handleViewerDisconnect(webtoonId, clientId);
		});
		emitter.onTimeout(() -> {
			handleViewerDisconnect(webtoonId, clientId);
		});
		emitter.onError(e -> {
			handleViewerDisconnect(webtoonId, clientId);
		});

		return emitter;
	}

	public void handleViewerDisconnect(Long webtoonId, String clientId) {
		String key = webtoonId + "-" + clientId;

		if (!cleanedUp.add(key)) {
			log.debug("중복 cleanup 무시: {}", key);
			return;
		}

		viewerEventPublisher.publishLeave(webtoonId, clientId);
		webtoonSseEmitterRepository.remove(webtoonId, clientId);
	}

	public void leaveWebtoon(Long webtoonId, String clientId) {
		viewerEventPublisher.publishLeave(webtoonId, clientId);
	}
}
