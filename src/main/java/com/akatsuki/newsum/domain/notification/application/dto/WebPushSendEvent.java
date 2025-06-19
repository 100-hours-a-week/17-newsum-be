package com.akatsuki.newsum.domain.notification.application.dto;

import java.time.LocalDateTime;

public record WebPushSendEvent(
	Long userId,
	Long targetId,
	String title,
	String content,
	String targetType,
	LocalDateTime createdAt
) {
}
