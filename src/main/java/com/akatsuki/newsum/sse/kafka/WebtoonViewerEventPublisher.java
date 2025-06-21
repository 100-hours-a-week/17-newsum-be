package com.akatsuki.newsum.sse.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.akatsuki.newsum.cache.ViewerEventDeduplicationCache;
import com.akatsuki.newsum.sse.kafka.dto.ViewerAction;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//웹툰 시청자 카프카 발행용
@Slf4j
@Service
@RequiredArgsConstructor
public class WebtoonViewerEventPublisher {

	private final KafkaTemplate<String, WebtoonViewerEvent> kafkaTemplate;
	private final ViewerEventDeduplicationCache dedupCache;

	public void publishJoin(Long webtoonId, String clientId) {
		String key = webtoonId + "-" + clientId + "-JOIN";
		if (dedupCache.isDuplicate(key)) {
			log.debug("중복 JOIN 발행 무시: {}", key);
			return;
		}
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.JOIN));
	}

	public void publishLeave(Long webtoonId, String clientId) {
		String key = webtoonId + "-" + clientId + "-LEAVE";
		if (dedupCache.isDuplicate(key)) {
			log.debug("중복 LEAVE 발행 무시: {}", key);
			return;
		}
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.LEAVE));
	}
}
