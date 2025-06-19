package com.akatsuki.newsum.domain.notification.application;

import org.springframework.stereotype.Service;

import com.akatsuki.newsum.domain.notification.application.dto.WebPushNotificationResponse;
import com.akatsuki.newsum.domain.notification.application.dto.WebPushSendCommand;
import com.akatsuki.newsum.domain.notification.application.usecase.NotificationSendUseCase;
import com.akatsuki.newsum.sse.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendService implements NotificationSendUseCase {

	private final SseService sseService;

	@Override
	public void send(WebPushSendCommand command) {
		sseService.sendDataToUser(String.valueOf(command.userId()), new WebPushNotificationResponse(
			command.targetId(),
			command.title(),
			command.content(),
			command.targetType(),
			command.createdAt()
		));
	}
}
