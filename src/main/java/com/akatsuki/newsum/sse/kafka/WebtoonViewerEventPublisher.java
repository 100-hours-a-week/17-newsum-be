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
		log.info("📡 Kafka 전송 메서드 수행 전 키값: {}", key);
		if (dedupCache.isDuplicate(key)) {
			//값이 있어서 true일 경우
			log.debug("중복 JOIN 발행 무시: {}", key);
			return;
		}
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.JOIN));
		log.info("📡 Kafka 전송 send 메서드 수행 완료 : {}", key);
	}

	public void publishLeave(Long webtoonId, String clientId) {
		String key = webtoonId + "-" + clientId + "-LEAVE";
		log.info("📡 Kafka leave 전송 메서드  메서드 수행 전 키값  : {}", key);
		if (dedupCache.isDuplicate(key)) {
			log.debug("중복 LEAVE 발행 무시: {}", key);
			return;
		}
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.LEAVE));
		log.info("📡 Kafka 전송 leave 메서드  메서드 수행 완료: {}", key);
	}
}
