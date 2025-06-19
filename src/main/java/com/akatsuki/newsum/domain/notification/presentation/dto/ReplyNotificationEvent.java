package com.akatsuki.newsum.domain.notification.presentation.dto;

public record ReplyNotificationEvent(
	Long userId,
	Long commentId
) {
}

