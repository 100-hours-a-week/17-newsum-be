package com.akatsuki.newsum.sse.kafka;

import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.akatsuki.newsum.cache.RedisService;
import com.akatsuki.newsum.sse.kafka.dto.ViewerAction;
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
		WebtoonViewerEvent event = new WebtoonViewerEvent(webtoonId, clientId, ViewerAction.JOIN);

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
			emitters);  //getEmittersWithClientIds의 메서드는 뭐인가?

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
}
