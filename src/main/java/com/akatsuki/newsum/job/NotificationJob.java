package com.akatsuki.newsum.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.domain.notification.application.usecase.NotificationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationJob {

	public final NotificationUseCase notificationUseCase;

	@Scheduled(cron = "0 0/1 * * * *")
	public void deleteNotification() {
		log.info("일주얼 전 알림 삭제 시작");
		int rows = notificationUseCase.deleteBefore7Days();
		log.info("알림 {} 건 삭제 완료", rows);
	}
}
