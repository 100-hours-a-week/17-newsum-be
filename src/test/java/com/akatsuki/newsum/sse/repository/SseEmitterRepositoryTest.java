package com.akatsuki.newsum.sse.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseEmitterRepositoryTest {

	private SseEmitterRepository repository;

	@BeforeEach
	void setUp() {
		repository = new SseEmitterRepository();
		repository.init();
	}

	@Test
	@DisplayName("로그인 사용자 emitter 저장 및 조회 테스트")
	void testSaveAndGetEmitter() {
		//given
		String userId = "user-1";
		String clientId = "client-A";

		//when
		SseEmitter emitter = repository.save(userId, clientId);

		//then
		assertThat(repository.get(userId, clientId).get()).isEqualTo(emitter);
	}

	@Test
	@DisplayName("로그인 사용자 emitter 저장 및 조회 테스트")
	void testsaveAnonymous() {
		//given
		String clientId = "client-익명유저";

		//when
		SseEmitter emitter = repository.saveAnonymous(clientId);

		//then
		Optional<SseEmitter> emitter2 = repository.getAnonymous(clientId);
		assertThat(emitter2).isPresent();
		assertThat(repository.getAnonymous(clientId).get()).isEqualTo(emitter);
	}

}
