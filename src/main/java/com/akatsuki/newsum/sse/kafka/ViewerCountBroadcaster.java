package com.akatsuki.newsum.sse.kafka;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.cache.RedisService;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;
import com.akatsuki.newsum.sse.repository.WebtoonSseEmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewerCountBroadcaster {

	private final RedisService redisService;
	private final WebtoonSseEmitterRepository webtoonSseEmitterRepository;

	@KafkaListener(
		topics = "webtoon-viewer",
		groupId = "viewer-count-broadcast",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void onEvent(WebtoonViewerEvent event) {
		Long webtoonId = event.webtoonId();
		String clientId = event.clientId();
		String key = "webtoon:viewers:" + webtoonId;

		switch (event.action()) {
			case JOIN -> {
				redisService.addSetValue(key, clientId);
				redisService.setExpire(key, Duration.ofDays(1)); // TTL 설정
			}
			case LEAVE -> redisService.removeSetValue(key, clientId);
			default -> log.warn("알 수 없는 이벤트: {}", event);
		}

		Long count = redisService.getSetSize(key);
		log.info("📡 레디스 현재 사용자 수 갯수 전달 전 갯수 확인 : {}", count);
		long viewerCount = count != null ? count : 0;

		//사용자들에게 갯수 전달
		Map<String, SseEmitter> emitters = webtoonSseEmitterRepository.getEmittersWithClientIds(webtoonId);
		emitters.forEach((cid, emitter) -> {
			try {
				emitter.send(SseEmitter.event().name("viewer-count").data("viewerCount: " + viewerCount));
			} catch (IOException | IllegalStateException e) {
				emitter.completeWithError(e);
				webtoonSseEmitterRepository.remove(webtoonId, cid);
			}
		});
	}
}
