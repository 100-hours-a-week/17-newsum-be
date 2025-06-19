package com.akatsuki.newsum.domain.notification.application.dto;

import java.time.LocalDateTime;

public record WebPushSendCommand(
	Long userId,
	Long targetId,
	String title,
	String content,
	String targetType,
	LocalDateTime createdAt
) {

	public static WebPushSendCommand of(WebPushSendEvent event) {
		return new WebPushSendCommand(
			event.userId(),
			event.targetId(),
			event.title(),
			event.content(),
			event.targetType(),
			event.createdAt()
		);
	}
}
