package com.akatsuki.newsum.sse.kafka;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import com.akatsuki.newsum.cache.ViewerEventDeduplicationCache;
import com.akatsuki.newsum.sse.kafka.dto.ViewerActionString;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;

public class WebtoonViewerEventPublisherTest {

	private KafkaTemplate<String, WebtoonViewerEvent> kafkaTemplate;
	private ViewerEventDeduplicationCache dedupCache;
	private WebtoonViewerEventPublisher publisher;

	@BeforeEach
	void setUp() {
		kafkaTemplate = mock(KafkaTemplate.class);
		dedupCache = mock(ViewerEventDeduplicationCache.class);
		//Test할 클래스 넣기
		publisher = new WebtoonViewerEventPublisher(kafkaTemplate, dedupCache);
	}

	@Test
	@DisplayName("JOIN - 중복 아님: Kafka 전송 수행")
	void publishJoin_whenNotDuplicate_shouldSendKafka() {
		Long webtoonId = 10L;
		String clientId = "client-A";
		String key = webtoonId + "-" + clientId + "-JOIN";

		//중복이 아님을 명시
		when(dedupCache.isDuplicate(key)).thenReturn(false);

		publisher.publishJoin(webtoonId, clientId);

		verify(kafkaTemplate).send("webtoon-viewer",
			new WebtoonViewerEvent(webtoonId, clientId, new ViewerActionString("JOIN")));
	}

	@Test
	@DisplayName("JOIN - 중복이면 Kafka 전송 안함")
	void publishJoin_whenDuplicate_shouldNotSendKafka() {
		Long webtoonId = 10L;
		String clientId = "client-A";
		String key = webtoonId + "-" + clientId + "-JOIN";

		when(dedupCache.isDuplicate(key)).thenReturn(true);

		publisher.publishJoin(webtoonId, clientId);

		verify(kafkaTemplate, never()).send(any(), any());
	}

	@Test
	@DisplayName("LEAVE - 중복 아님: Kafka 전송 수행")
	void publishLeave_whenNotDuplicate_shouldSendKafka() {
		Long webtoonId = 11L;
		String clientId = "client-B";
		String key = webtoonId + "-" + clientId + "-LEAVE";

		when(dedupCache.isDuplicate(key)).thenReturn(false);

		publisher.publishLeave(webtoonId, clientId);

		verify(kafkaTemplate).send("webtoon-viewer",
			new WebtoonViewerEvent(webtoonId, clientId, new ViewerActionString("JOIN")));
	}

	@Test
	@DisplayName("LEAVE - 중복이면 Kafka 전송 안함")
	void publishLeave_whenDuplicate_shouldNotSendKafka() {
		Long webtoonId = 11L;
		String clientId = "client-B";
		String key = webtoonId + "-" + clientId + "-LEAVE";

		when(dedupCache.isDuplicate(key)).thenReturn(true);

		publisher.publishLeave(webtoonId, clientId);

		verify(kafkaTemplate, never()).send(any(), any());
	}
}
