package com.akatsuki.newsum.sse.repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class WebtoonSseEmitterRepository {

	private static final long TIMEOUT = 5 * 60 * 1000L;
	private final Map<Long, Map<String, SseEmitter>> viewers = new ConcurrentHashMap<>();

	public void save(Long webtoonId, String clientId, SseEmitter emitter) {
		viewers.computeIfAbsent(webtoonId, k -> new ConcurrentHashMap<>())
			.put(clientId, emitter);
	}

	public void remove(Long webtoonId, String clientId) {
		Map<String, SseEmitter> clients = viewers.get(webtoonId);
		if (clients != null) {
			clients.remove(clientId);
			if (clients.isEmpty()) {
				viewers.remove(webtoonId);
			}
		}
	}

	public Collection<SseEmitter> getEmitters(Long webtoonId) {
		return viewers.getOrDefault(webtoonId, Map.of()).values();
	}
}
