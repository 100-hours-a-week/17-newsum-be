package com.akatsuki.newsum.sse.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class WebtoonSseEmitterRepositoryTest {

	private WebtoonSseEmitterRepository repository;

	@BeforeEach
	void setUp() {
		repository = new WebtoonSseEmitterRepository();
	}

	@Test
	@DisplayName("save()로 emitter 저장하면 getEmittersWithClientIds()에서 조회 가능")
	void testSaveAndGetEmitters() {
		// given
		Long webtoonId = 1L;
		String clientId = "client-A";

		// when
		SseEmitter emitter = repository.save(webtoonId, clientId);

		// then
		Map<String, SseEmitter> emitters = repository.getEmittersWithClientIds(webtoonId);
		assertThat(emitters).containsKey(clientId);
		assertThat(emitters.get(clientId)).isEqualTo(emitter);
	}

	@Test
	@DisplayName("remove()로 emitter 삭제 시 클라이언트가 제거됨")
	void testRemoveEmitter() {
		// given
		Long webtoonId = 1L;
		String clientId = "client-B";
		repository.save(webtoonId, clientId);

		// when
		repository.remove(webtoonId, clientId);

		// then
		Map<String, SseEmitter> emitters = repository.getEmittersWithClientIds(webtoonId);
		assertThat(emitters).doesNotContainKey(clientId);
	}

	@Test
	@DisplayName("emitter 모두 삭제되면 webtoonId 키도 제거됨")
	void testRemoveLastEmitterCleansUpWebtoonKey() {
		// given
		Long webtoonId = 2L;
		String clientId = "client-C";
		repository.save(webtoonId, clientId);

		// when
		repository.remove(webtoonId, clientId);

		// then
		Map<String, SseEmitter> emitters = repository.getEmittersWithClientIds(webtoonId);
		assertThat(emitters).isEmpty(); // 내부적으로 emitters Map에 해당 키가 없어짐
	}
}
