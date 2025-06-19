package com.akatsuki.newsum.sse.repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.annotation.PostConstruct;

@Component
public class SseEmitterRepository {

	private final Long DEFAULT_TIMEOUT = 60 * 60 * 1000L; // 1시간
	private final String ANONYMOUS_USER_ID = "anonymous";
	private final String ALL_USER_ID = "all";

	private final Map<String, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		emitters.put(ANONYMOUS_USER_ID, new ConcurrentHashMap<>());
		emitters.put(ALL_USER_ID, new ConcurrentHashMap<>());
	}

	//anonymous
	public SseEmitter saveAnonymous(String clientId) {
		SseEmitter emitter = defaultEmitter(clientId);
		saveEmitter(ANONYMOUS_USER_ID, clientId, emitter);
		return emitter;
	}

	//login user
	public SseEmitter save(String userId, String clientId) {
		SseEmitter emitter = defaultEmitter(userId, clientId);
		saveEmitter(userId, clientId, emitter);
		return emitter;
	}

	public Optional<SseEmitter> getAnonymous(String userId) {
		return get(ANONYMOUS_USER_ID, userId);
	}

	public Optional<SseEmitter> get(String userId, String uuid) {
		return Optional.ofNullable(
			emitters.getOrDefault(userId, new ConcurrentHashMap<>())
				.get(uuid)
		);
	}

	public Set<SseEmitter> get(String userId) {
		Map<String, SseEmitter> emitterValues = emitters.get(userId);
		return new HashSet<>(emitterValues.values());
	}

	public void remove(String clientId) {
		removeClient(ALL_USER_ID, clientId);
		removeClient(ANONYMOUS_USER_ID, clientId);
	}

	public void remove(String userId, String clientId) {
		removeClient(ALL_USER_ID, clientId);
		removeClient(userId, clientId);
	}

	private void removeClient(String userId, String clientId) {
		Map<String, SseEmitter> clients = emitters.get(userId);

		clients.values()
			.forEach(ResponseBodyEmitter::complete);
		clients.remove(clientId);
	}

	private SseEmitter defaultEmitter() {
		return new SseEmitter(DEFAULT_TIMEOUT);
	}

	private SseEmitter defaultEmitter(String clientId) {
		SseEmitter emitter = defaultEmitter();

		emitter.onCompletion(() -> remove(clientId));
		emitter.onTimeout(() -> remove(clientId));
		emitter.onError(e -> remove(clientId));

		return emitter;
	}

	private SseEmitter defaultEmitter(String userId, String clientId) {
		SseEmitter emitter = defaultEmitter();

		emitter.onCompletion(() -> remove(userId, clientId));
		emitter.onTimeout(() -> remove(userId, clientId));
		emitter.onError(e -> remove(userId, clientId));

		return emitter;
	}

	private void saveEmitter(String userId, String clientId, SseEmitter emitter) {
		Map<String, SseEmitter> emitterMap = emitters.getOrDefault(userId, new ConcurrentHashMap<>());

		emitterMap.put(clientId, emitter);
		emitters.put(userId, emitterMap);
		emitters.get(ALL_USER_ID)
			.put(userId, emitter);
	}
}
