package com.akatsuki.newsum.domain.notification.presentation.dto;

import java.time.LocalDateTime;

public record NotificationResult(
	Long id,
	String type,
	String title,
	String content,
	LocalDateTime createdAt,
	Boolean isRead
) {
}
