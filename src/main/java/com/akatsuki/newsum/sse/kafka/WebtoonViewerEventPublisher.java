package com.akatsuki.newsum.sse.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.akatsuki.newsum.sse.kafka.dto.ViewerAction;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;

import lombok.RequiredArgsConstructor;

//웹툰 시청자 카프카 발행용
@Service
@RequiredArgsConstructor
public class WebtoonViewerEventPublisher {

	private final KafkaTemplate<String, WebtoonViewerEvent> kafkaTemplate;

	public void publishJoin(Long webtoonId, String clientId) {
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.JOIN));
	}

	public void publishLeave(Long webtoonId, String clientId) {
		kafkaTemplate.send("webtoon-viewer", new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.LEAVE));
	}
}
