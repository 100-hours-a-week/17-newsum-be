package com.akatsuki.newsum.sse.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.sse.kafka.WebtoonViewerEventPublisher;
import com.akatsuki.newsum.sse.repository.SseEmitterRepository;
import com.akatsuki.newsum.sse.repository.WebtoonSseEmitterRepository;
import com.akatsuki.newsum.sse.service.viewer.WebtoonViewerTracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

	private final SseEmitterRepository sseEmitterRepository;
	private final WebtoonViewerTracker webtoonViewerTracker;
	private final WebtoonViewerEventPublisher viewerEventPublisher;
	private final WebtoonSseEmitterRepository webtoonSseEmitterRepository;

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
		SseEmitter emitter = subscribe(clientId);

		webtoonSseEmitterRepository.save(webtoonId, clientId);

		webtoonViewerTracker.addViewer(webtoonId, clientId);
		viewerEventPublisher.publishJoin(webtoonId, clientId);

		try {
			int count = webtoonViewerTracker.getViewerCount(webtoonId);
			emitter.send(SseEmitter.event()
				.name("viewer-count")
				.data("viewerCount: " + count));
		} catch (IOException | IllegalStateException e) {
			log.warn("초기 viewerCount 전송 실패: {}", e.getMessage());
			emitter.completeWithError(e);
		}

		registerEmitterCleanup(emitter, webtoonId, clientId);
		return emitter;
	}

	private void registerEmitterCleanup(SseEmitter emitter, Long webtoonId, String clientId) {
		Runnable cleanupTask = () -> cleanup(webtoonId, clientId);

		emitter.onCompletion(cleanupTask);
		emitter.onTimeout(cleanupTask);
		emitter.onError(e -> {
			log.warn("SSE error 발생: {}", e.toString());
			cleanupTask.run();
		});
	}

	private void cleanup(Long webtoonId, String clientId) {
		log.info("SSE 종료: webtoonId={}, clientId={}", webtoonId, clientId);

		webtoonViewerTracker.removeViewer(webtoonId, clientId);
		viewerEventPublisher.publishLeave(webtoonId, clientId);
		webtoonSseEmitterRepository.remove(webtoonId, clientId);
		sendViewerCount(webtoonId);
	}

	private void sendViewerCount(Long webtoonId) {
		int count = webtoonViewerTracker.getViewerCount(webtoonId);
		String message = "viewerCount: " + count;

		webtoonSseEmitterRepository.getEmitters(webtoonId).forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event().name("viewer-count").data(message));
			} catch (IllegalStateException | IOException e) {
				log.warn("SSE 전송 실패: {}", e.getMessage());
				emitter.completeWithError(e);
			}
		});
	}
}
