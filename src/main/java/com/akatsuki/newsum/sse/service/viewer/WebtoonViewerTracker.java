package com.akatsuki.newsum.sse.service.viewer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WebtoonViewerTracker {
	//접속자 수 저장 및 관리
	private final Map<Long, Set<String>> viewers = new ConcurrentHashMap<>();

	//특정 웹툰에 접속한 사용자들 등록(set 사용해서 중복 제거)
	public void addViewers(Long webtoonId, String clientId) {
		//해당 웹툰에 대해 set이 존재하지 않을 경우 만들고 클라아이디 넣기
		viewers.computeIfAbsent(webtoonId, k -> ConcurrentHashMap.newKeySet()).add(clientId);
	}

	//웹툰에서 나간 사용자 제거하기
	public void removeViewer(Long webtoonId, String clientId) {
		Set<String> clientIds = viewers.get(webtoonId);
		if (clientIds != null) {
			clientIds.remove(clientId);
			if (clientIds.isEmpty()) {
				viewers.remove(webtoonId);
			}
		}
	}

	//현재 웹툰을 보고 있는 접속자 수를 반환
	public int getViewerCount(Long webtoonId) {
		return viewers.getOrDefault(webtoonId, Set.of()).size();
	}
}

