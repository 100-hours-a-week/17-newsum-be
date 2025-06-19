package com.akatsuki.newsum.domain.notification.application.usecase;

import com.akatsuki.newsum.domain.notification.application.dto.WebPushSendCommand;

public interface NotificationSendUseCase {
	void send(WebPushSendCommand event);
}
