package com.akatsuki.newsum.domain.log.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.akatsuki.newsum.domain.log.dto.WebtoonViewLogRequest;
import com.akatsuki.newsum.domain.log.entity.WebtoonViewLog;
import com.akatsuki.newsum.log.repository.WebtoonViewLogRepository;

@Service
public class WebtoonViewLogService {

	private final WebtoonViewLogRepository logRepository;

	public WebtoonViewLogService(WebtoonViewLogRepository logRepository) {
		this.logRepository = logRepository;
	}

	public void logView(WebtoonViewLogRequest request) {
		WebtoonViewLog log = new WebtoonViewLog(
			request.userId(),
			request.webtoonId(),
			LocalDateTime.now()
		);
		logRepository.save(log);
	}
}
