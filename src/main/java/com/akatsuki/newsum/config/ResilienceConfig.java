package com.akatsuki.newsum.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@Configuration
public class ResilienceConfig {

	// Retry 설정
	// 예외 발생 시 최대 3회까지 재시도, 각 시도 사이에 10초 대기
	@Bean
	public RetryConfig retryConfig() {
		return RetryConfig.custom()
			.maxAttempts(3)
			.waitDuration(Duration.ofSeconds(10))
			.build();
	}

	// CircuitBreaker 설정
	// 실패율이 50%를 초과하면 Circuit을 열고, 30초 동안 요청 차단
	@Bean
	public CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig.custom()
			.failureRateThreshold(50)
			.slidingWindowSize(5)
			.waitDurationInOpenState(Duration.ofSeconds(30))
			.build();
	}

	// TimeLimiter 설정
	// 각 작업이 최대 2초 이내에 완료되어야 함
	@Bean
	public TimeLimiterConfig timeLimiterConfig() {
		return TimeLimiterConfig.custom()
			.timeoutDuration(Duration.ofSeconds(2))
			.build();
	}
}
