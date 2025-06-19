package com.akatsuki.newsum.domain.notification.application.dto;

public record ReplyNotificationCommand(
	Long userId,
	Long commentId
) {
}
