package com.akatsuki.newsum.sse.service.viewer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WebtoonViewerTracker {

	private final Map<Long, Set<String>> viewers = new ConcurrentHashMap<>();

	public void addViewer(Long webtoonId, String clientId) {
		viewers.computeIfAbsent(webtoonId, k -> ConcurrentHashMap.newKeySet()).add(clientId);
	}

	public void removeViewer(Long webtoonId, String clientId) {
		viewers.computeIfPresent(webtoonId, (id, clientIds) -> {
			clientIds.remove(clientId);
			return clientIds.isEmpty() ? null : clientIds;
		});
	}

	public int getViewerCount(Long webtoonId) {
		return viewers.getOrDefault(webtoonId, Set.of()).size();
	}
}

