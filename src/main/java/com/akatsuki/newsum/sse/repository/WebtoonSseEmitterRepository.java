package com.akatsuki.newsum.sse.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class WebtoonSseEmitterRepository {

	private static final long TIMEOUT = 60 * 60 * 1000L;  //4분
	private final Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

	public SseEmitter save(Long webtoonId, String clientId) {
		SseEmitter emitter = new SseEmitter(TIMEOUT);

		emitters.computeIfAbsent(webtoonId, k -> new ConcurrentHashMap<>())
			.put(clientId, emitter);

		return emitter;
	}

	public void remove(Long webtoonId, String clientId) {
		Map<String, SseEmitter> clients = emitters.get(webtoonId);
		if (clients != null) {
			clients.remove(clientId);
			if (clients.isEmpty()) {
				emitters.remove(webtoonId);
			}
		}
	}

	public Map<String, SseEmitter> getEmittersWithClientIds(Long webtoonId) {
		return new HashMap<>(emitters.getOrDefault(webtoonId, Map.of()));
	}
}
