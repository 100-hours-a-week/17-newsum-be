package com.akatsuki.newsum.domain.notification.presentation.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.domain.notification.application.dto.ReplyNotificationCommand;
import com.akatsuki.newsum.domain.notification.application.usecase.NotificationUseCase;
import com.akatsuki.newsum.domain.notification.presentation.dto.ReplyNotificationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

	public final NotificationUseCase notificationUseCase;

	@EventListener
	public void handleReplyNotificationEvent(ReplyNotificationEvent event) {
		notificationUseCase.notify(new ReplyNotificationCommand(event.userId(), event.commentId()));
	}
}
