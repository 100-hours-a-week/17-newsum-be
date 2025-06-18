package com.akatsuki.newsum.sse.service.viewer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WebtoonViewerTracker {

	private final Map<Long, Set<String>> viewers = new ConcurrentHashMap<>();

	public void addViewers(Long webtoonId, String clientId) {
		viewers.computeIfAbsent(webtoonId, k -> ConcurrentHashMap.newKeySet()).add(clientId);
	}

	public void removeViewer(Long webtoonId, String clientId) {
		Set<String> clientIds = viewers.get(webtoonId);
		if (clientIds != null) {
			clientIds.remove(clientId);
			if (clientIds.isEmpty()) {
				viewers.remove(webtoonId);
			}
		}
	}

	public int getViewerCount(Long webtoonId) {
		return viewers.getOrDefault(webtoonId, Set.of()).size();
	}
}

