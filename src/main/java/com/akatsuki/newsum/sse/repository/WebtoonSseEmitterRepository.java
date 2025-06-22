package com.akatsuki.newsum.sse.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class WebtoonSseEmitterRepository {

	private static final long TIMEOUT = 4 * 60 * 1000L;  //4분
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

	public Collection<SseEmitter> getEmitters(Long webtoonId) {
		return emitters.getOrDefault(webtoonId, Map.of()).values();
	}

	public int getViewerCount(Long webtoonId) {
		return emitters.getOrDefault(webtoonId, Map.of()).size();
	}

	public Map<String, SseEmitter> getEmittersWithClientIds(Long webtoonId) {
		return new HashMap<>(emitters.getOrDefault(webtoonId, Map.of()));
	}
}
