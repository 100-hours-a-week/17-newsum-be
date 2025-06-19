package com.akatsuki.newsum.domain.notification.application.usecase;

import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListCommand;
import com.akatsuki.newsum.domain.notification.application.dto.NotificationPageListResult;
import com.akatsuki.newsum.domain.notification.application.dto.ReplyNotificationCommand;

public interface NotificationUseCase {
	NotificationPageListResult lookupNotificationsByUserId(NotificationPageListCommand notificationPageListCommand);

	void notify(ReplyNotificationCommand replyNotificationCommand);

	void removeAllNotifications(Long userId);

	void removeNotification(Long userId, Long notificationId);

	void readNotification(Long userId, Long notificationId);

	Boolean hasNotReadNotification(Long userId);

	int deleteBefore7Days();
}
