package com.akatsuki.newsum.sse.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.cache.ViewerEventDeduplicationCache;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;
import com.akatsuki.newsum.sse.service.viewer.WebtoonViewerTracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor

//카프카 수신용
public class WebtoonViewerEventConsumer {

	private final ViewerEventDeduplicationCache dedupCache;
	private final WebtoonViewerTracker viewerTracker;

	@KafkaListener(
		topics = "webtoon-viewer",
		groupId = "webtoon-viewer-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(WebtoonViewerEvent event) {
		String key = generateKey(event);

		if (dedupCache.isDuplicate(key)) {
			log.debug("중복 이벤트 무시: {}", key);
			return;
		}
		log.debug("Kafka 수신: {}", event);

		switch (event.action()) {
			case JOIN -> viewerTracker.addViewer(event.webtoonId(), event.clientId());
			case LEAVE -> viewerTracker.removeViewer(event.webtoonId(), event.clientId());
			default -> log.warn("알 수 없는 이벤트 타입: {}", event.action());
		}
	}

	private String generateKey(WebtoonViewerEvent event) {
		return event.webtoonId() + "-" + event.clientId() + "-" + event.action();
	}
}
