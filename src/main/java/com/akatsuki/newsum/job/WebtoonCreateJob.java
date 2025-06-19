package com.akatsuki.newsum.job;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.domain.webtoon.service.ImageQueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebtoonCreateJob {

	private final ImageQueueService imageQueueService;
	private final ReentrantLock lock = new ReentrantLock();

	private static final long THIRTY_MINUTES = 5 * 60 * 1000; // 5분

	@Scheduled(fixedRate = THIRTY_MINUTES)
	public void process() {
		if (!lock.tryLock()) {
			log.info("이전 작업 실행 중 - 스킵");
			return;
		}

		log.info("Webtoon 생성 스케줄링 시작");

		try {
			imageQueueService.processWebtoonQueue();
		} finally {
			log.info("Webtoon 생성 스케쥴링 종료");
			lock.unlock();
		}
	}
}
