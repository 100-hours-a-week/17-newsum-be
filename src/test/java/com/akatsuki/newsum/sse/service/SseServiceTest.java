package com.akatsuki.newsum.sse.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.sse.kafka.WebtoonViewerEventPublisher;
import com.akatsuki.newsum.sse.repository.SseEmitterRepository;
import com.akatsuki.newsum.sse.repository.WebtoonSseEmitterRepository;

public class SseServiceTest {

	@InjectMocks
	private SseService sseService;

	@Mock
	private SseEmitterRepository sseEmitterRepository;

	@Mock
	private WebtoonViewerEventPublisher viewerEventPublisher;

	@Mock
	private WebtoonSseEmitterRepository webtoonSseEmitterRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this); // ✅ 최신 방식
	}

	@Test
	@DisplayName("비로그인 사용자 구독 저장")
	void 익명사용자구독() throws Exception {
		String uuid = "anonymous-uuid";
		SseEmitter sseEmitter = mock(SseEmitter.class);
		when(sseEmitterRepository.saveAnonymous(uuid)).thenReturn(sseEmitter);

		SseEmitter result = sseService.subscribe(uuid);

		assertThat(result).isEqualTo(sseEmitter);
		verify(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(sseEmitter, never()).completeWithError(any());
	}

	@Test
	@DisplayName("로그인 사용자 구독 저장")
	void 로그인사용자구독() throws Exception {
		String userId = "user-1";
		String clientId = "client-abc";
		SseEmitter sseEmitter = mock(SseEmitter.class);
		when(sseEmitterRepository.save(userId, clientId)).thenReturn(sseEmitter);

		SseEmitter result = sseService.subscribe(userId, clientId);

		assertThat(result).isEqualTo(sseEmitter);
		verify(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(sseEmitter, never()).completeWithError(any());
	}

	@Test
	void sendDataToUser_예외발생시_제거() throws Exception {
		String userId = "user-1";
		Object data = "Hello Webtoon!";
		SseEmitter sseEmitter = mock(SseEmitter.class);

		when(sseEmitterRepository.get(userId)).thenReturn(Set.of(sseEmitter)); // ✅ Set으로 변경

		doThrow(new IOException("전송 실패")).when(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));

		sseService.sendDataToUser(userId, data);

		verify(sseEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(sseEmitter).completeWithError(any());
		verify(sseEmitterRepository).remove(userId);
	}

	@Test
	@DisplayName("leaveWebtoon에 대한 테스트코드")
	void leaveWebtoon() throws Exception {
		//given
		Long webtoonId = 1234L;
		String clientId = "client-001";

		// when
		sseService.leaveWebtoon(webtoonId, clientId);

		// then
		verify(viewerEventPublisher).publishLeave(webtoonId, clientId);
	}

	@Test
	void startviewingWebtoon() throws Exception {
		Long webtoonId = 1234L;
		String clientId = "client-001";
		SseEmitter sseEmitter = mock(SseEmitter.class);

		// save() 호출 시 mockEmitter 반환하도록 설정
		when(webtoonSseEmitterRepository.save(webtoonId, clientId)).thenReturn(sseEmitter);

		//then
		SseEmitter result = sseService.startViewingWebtoon(webtoonId, clientId);

		assertThat(result).isEqualTo(sseEmitter); // 반환값이 동일한지 확인
		verify(webtoonSseEmitterRepository).save(webtoonId, clientId); // 저장 호출 확인
		verify(viewerEventPublisher).publishJoin(webtoonId, clientId); // join 이벤트 발행 확인
	}

	@Test
	void handleViewerDisconnect_최초호출된경우() throws Exception {
		Long webtoonId = 1234L;
		String clientId = "client-001";
		String key = "viewer-001";

		// when
		sseService.handleViewerDisconnect(webtoonId, clientId);

		//then
		verify(viewerEventPublisher).publishLeave(webtoonId, clientId);
		verify(webtoonSseEmitterRepository).remove(webtoonId, clientId);
	}
}
