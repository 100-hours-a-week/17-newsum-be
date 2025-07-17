package com.akatsuki.newsum.sse.kafka;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.cache.RedisService;
import com.akatsuki.newsum.sse.kafka.dto.ViewerActionString;
import com.akatsuki.newsum.sse.kafka.dto.WebtoonViewerEvent;
import com.akatsuki.newsum.sse.repository.WebtoonSseEmitterRepository;

public class ViewerCountBroadcasterTest {
	private RedisService redisService;
	private ViewerCountBroadcaster broadcaster;
	private WebtoonSseEmitterRepository emitterRepository;

	@BeforeEach
	void setUp() {
		//테스트객체생성
		redisService = mock(RedisService.class);
		emitterRepository = mock(WebtoonSseEmitterRepository.class);
		broadcaster = new ViewerCountBroadcaster(redisService, emitterRepository);
	}

	//Join 입장 이벤트 테스트
	@Test
	@DisplayName("사용자가 입장하면 시청자수를 모든 클라이언트에 전달하는 코드")
	void testJoinViewer() throws Exception {
		//given
		Long webtoonId = 1L;
		String clientId = "clientId-123";
		String rediskey = "webtoon:viewers:" + webtoonId;

		//client-A 입장
		WebtoonViewerEvent event = new WebtoonViewerEvent(webtoonId, clientId, new ViewerActionString("JOIN"));

		//3명의 sseEmiiter 객체 생성
		SseEmitter user1 = mock(SseEmitter.class);
		SseEmitter user2 = mock(SseEmitter.class);
		SseEmitter user3 = mock(SseEmitter.class);

		//현재 사용자를 저장한 Map
		Map<String, SseEmitter> emitters = Map.of(
			"client-I", user1,
			"client-B", user2,
			"client-C", user3
		);
		//when 은 변수 선언이 불가능함
		Long viewerCount = 3L;
		when(redisService.getSetSize(rediskey)).thenReturn(viewerCount);
		when(emitterRepository.getEmittersWithClientIds(webtoonId)).thenReturn(
			emitters);

		//when
		broadcaster.onEvent(event);

		//then
		verify(redisService).addSetValue(rediskey, clientId);
		verify(redisService).setExpire(eq(rediskey), any()); //any() 메서드의 의미

		//유저들에게 새로운 사용자입장을 알림
		//verify로 비교
		verify(user1).send(any(SseEmitter.SseEventBuilder.class));
		verify(user2).send(any(SseEmitter.SseEventBuilder.class));
		verify(user3).send(any(SseEmitter.SseEventBuilder.class));
	}

	//case에서 Leave 이벤트 시
	@Test
	@DisplayName("사용자가 퇴장하면 redis_remove 수행하고 사용자에게 갯수 전달")
	void testRemoveViewer() throws Exception {
		//given
		Long webtoonId = 1L;
		String clientId = "clientId-123";  //나간 유저
		String rediskey = "webtoon:viewers:" + webtoonId;

		//client-123 퇴장
		WebtoonViewerEvent event = new WebtoonViewerEvent(webtoonId, clientId, new ViewerActionString("LEAVE"));
		SseEmitter user1 = mock(SseEmitter.class);
		SseEmitter user2 = mock(SseEmitter.class);
		SseEmitter user3 = mock(SseEmitter.class);

		Map<String, SseEmitter> emitters = Map.of(
			"client-I", user1,
			"client-B", user2,
			clientId, user3
		);
		//퇴장해서 2명남음
		when(redisService.getSetSize(rediskey)).thenReturn(2L);
		//map에 두명저장
		when(emitterRepository.getEmittersWithClientIds(webtoonId)).thenReturn(emitters);

		//when
		broadcaster.onEvent(event);

		//then
		verify(redisService).removeSetValue(rediskey, clientId);
		verify(user1).send(any(SseEmitter.SseEventBuilder.class));
		verify(user2).send(any(SseEmitter.SseEventBuilder.class));

	}

	@Test
	@DisplayName("알 수 없는 이벤트일 경우 아무 작업 없이 로그만 출력")
	void testRemoveViewerWithClientId() throws Exception {
		// given
		Long webtoonId = 1L;
		String clientId = "clientId-123";
		String redisKey = "webtoon:viewers:" + webtoonId;

		// ViewerAction에 없는 가짜 이벤트 시뮬레이션을 위해 null 사용
		WebtoonViewerEvent event = new WebtoonViewerEvent(webtoonId, clientId, null);

		//when
		broadcaster.onEvent(event);

		// then
		//호출이 없어야하기 때문에 never사용
		verify(redisService, never()).addSetValue(any(), any());
		verify(redisService, never()).removeSetValue(any(), any());
		verify(emitterRepository).getEmittersWithClientIds(webtoonId);

	}

	@Test
	@DisplayName("SseEmitter에서 IOException 발생 시 completeWithError와 repository.remove 호출됨")
	void testEmitterSendIOException() throws Exception {
		// given
		Long webtoonId = 1L;
		String clientId = "client-X";
		String redisKey = "webtoon:viewers:" + webtoonId;

		WebtoonViewerEvent event = new WebtoonViewerEvent(webtoonId, clientId, new ViewerActionString("JOIN"));

		// 예외 던지는 mock emitter
		SseEmitter faultyEmitter = mock(SseEmitter.class);

		//실제 이벤트객체 생성
		SseEmitter.SseEventBuilder eventBuilder = SseEmitter
			.event()
			.name("viewer-count")
			.data("viewerCount: 1");

		doThrow(new IOException("Test exception"))
			.when(faultyEmitter)
			.send(any(SseEmitter.SseEventBuilder.class));

		Map<String, SseEmitter> emitters = Map.of(clientId, faultyEmitter);

		when(redisService.getSetSize(redisKey)).thenReturn(1L);
		when(emitterRepository.getEmittersWithClientIds(webtoonId)).thenReturn(emitters);

		// when
		broadcaster.onEvent(event);

		// then
		verify(faultyEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(faultyEmitter).completeWithError(any(IOException.class));
		verify(emitterRepository).remove(webtoonId, clientId);
	}

}
