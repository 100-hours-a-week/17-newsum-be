package com.akatsuki.newsum.sse.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.sse.repository.SseEmitterRepository;
import com.akatsuki.newsum.sse.service.viewer.WebtoonViewerTracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

	private final SseEmitterRepository sseEmitterRepository;
	private final WebtoonViewerTracker webtoonViewerTracker;

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
					sseEmitterRepository.remove(userId);
				}
			});
	}

	public SseEmitter subscribeToWebtoon(Long webtoonId, String userId, String clientId) {
		SseEmitter emitter;
		final String idForCleanup;

		if (userId == null) {
			emitter = subscribe(clientId);
			idForCleanup = "anonymous";
		} else {
			emitter = subscribe(userId, clientId);
			idForCleanup = userId;
		}

		webtoonViewerTracker.addViewers(webtoonId, clientId);
		sendViewerCount(webtoonId);

		registerEmitterCleanup(emitter, webtoonId, idForCleanup, clientId);

		return emitter;
	}

	private void sendViewerCount(Long webtoonId) {
		int count = webtoonViewerTracker.getViewerCount(webtoonId);
		String message = "viewerCount: " + count;

		sseEmitterRepository.getAllEmitters().forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event()
					.name("viewer-count")
					.data(message));
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
	}

	private void registerEmitterCleanup(SseEmitter emitter, Long webtoonId, String userId, String clientId) {
		Runnable cleanupTask = () -> cleanup(webtoonId, userId, clientId);
		emitter.onCompletion(cleanupTask);
		emitter.onTimeout(cleanupTask);
		emitter.onError(e -> cleanupTask.run());
	}

	private void cleanup(Long webtoonId, String userId, String clientId) {
		webtoonViewerTracker.removeViewer(webtoonId, clientId);
		sseEmitterRepository.remove(userId, clientId);
		sendViewerCount(webtoonId);
	}
}
