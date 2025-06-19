package com.akatsuki.newsum.domain.notification.presentation.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.domain.notification.application.dto.WebPushSendCommand;
import com.akatsuki.newsum.domain.notification.application.dto.WebPushSendEvent;
import com.akatsuki.newsum.domain.notification.application.usecase.NotificationSendUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebPushNotificationEventListener {

	private final NotificationSendUseCase notificationSendUseCase;

	@Async
	@EventListener
	public void handleWebPushNotificationEvent(WebPushSendEvent event) {
		notificationSendUseCase.send(WebPushSendCommand.of(event));
	}
}
