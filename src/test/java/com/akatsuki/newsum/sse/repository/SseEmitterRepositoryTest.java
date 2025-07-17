package com.akatsuki.newsum.sse.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

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

	@Test
	@DisplayName("로그인유저 id만 가지고 조회")
	void testgetUserId() {
		//given
		//map 안에 값을 저장
		String userId = "user-1";
		String userId2 = "user-2";
		String clientId1 = "client-1";
		String clientId2 = "client-2";

		//when
		repository.save(userId, clientId1);
		repository.save(userId2, clientId2);

		//then
		Set<SseEmitter> emitters = repository.get(userId);
		assertThat(emitters).hasSize(1);
	}

	@Test
	@DisplayName("remove(userId, clientId): 특정 userId에서 emitter 제거")
	void testRemoveUserEmitter() {
		// given
		String userId = "user-1";
		String clientId = "client-1";

		SseEmitter emitter = repository.save(userId, clientId);

		// when
		repository.remove(userId, clientId);

		// then
		Set<SseEmitter> emitters = repository.get(userId);
		assertThat(emitters).doesNotContain(emitter);
		assertThat(emitters).isEmpty();
	}

	@Test
	@DisplayName("remove(clientId): anonymous, all 영역에서 클라이언트 제거")
	void testRemoveAnonymousAndAllEmitter() {
		// given
		String clientId = "client-anon";

		SseEmitter emitter = repository.saveAnonymous(clientId);

		// when
		repository.remove(clientId); // anonymous, all 둘 다 제거됨

		// then
		assertThat(repository.getAnonymous(clientId)).isNotPresent();
	}
}
