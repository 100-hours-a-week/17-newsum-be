package com.akatsuki.newsum.domain.webtoon.service;

import static com.akatsuki.newsum.common.dto.ErrorCodeAndMessage.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.ImageGenerationQueue;
import com.akatsuki.newsum.domain.webtoon.exception.ImageGenerationException;
import com.akatsuki.newsum.domain.webtoon.repository.ImageGenerationQueueRepository;
import com.akatsuki.newsum.extern.dto.CreateWebtoonApiRequest;
import com.akatsuki.newsum.extern.service.AiServerApi;

import feign.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageQueueService {

	private final ImageGenerationQueueRepository imageGenerationQueueRepository;
	private final AiServerApi aiServerApi;

	@Transactional
	public void processWebtoonQueue() {
		checkAiServerStatusWithRetry();

		List<ImageGenerationQueue> tasks = imageGenerationQueueRepository.findImageGenerationQueueByCompletedAtIsNull();

		for (ImageGenerationQueue task : tasks) {
			boolean completed = false;
			int retryCount = 0;

			while (!completed && retryCount < 5) {
				try {
					CreateWebtoonApiRequest request = buildRequest(task);
					log.info("작업 요청 정보 {}", request);
					Response response = requestWithResilience(request).join(); // async 처리 후 block
					log.info("작업 응답 정보 {}", response);
					if (response.status() == 202) {
						log.info("작업 {} 성공, 다음 작업까지 5분 대기", task.getId());
						Thread.sleep(5 * 60 * 1000);
						task.processing();
						completed = true;
					} else if (response.status() == 503) {
						log.info("작업 {} 서버 busy, 1분 후 재시도", task.getId());
						Thread.sleep(60 * 1000);
						retryCount++;
					} else {
						log.warn("작업 {} 실패 - 응답코드 {}", task.getId(), response.status());
						Thread.sleep(60 * 1000);
						retryCount++;
					}

				} catch (Exception e) {
					log.error("작업 {} 예외 발생", task.getId(), e);
					retryCount++;
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				}
			}

			if (!completed) {
				log.error("작업 {} 실패 - 최대 재시도 초과", task.getId());
			}
		}
	}

	private CreateWebtoonApiRequest buildRequest(ImageGenerationQueue task) {
		return new CreateWebtoonApiRequest(
			String.valueOf(task.getId()),
			task.getImagePrompts()
		);
	}

	// 실제 외부 API 호출 로직에 Resilience4j 어노테이션 적용
	@Retry(name = "webtoon-retry")
	@CircuitBreaker(name = "webtoon-cb", fallbackMethod = "fallback")
	@TimeLimiter(name = "webtoon-tl", fallbackMethod = "timeoutFallback")
	protected CompletableFuture<Response> requestWithResilience(CreateWebtoonApiRequest request) {
		return CompletableFuture.supplyAsync(() -> aiServerApi.createWebtoon(request));
	}

	protected CompletableFuture<Response> fallback(CreateWebtoonApiRequest request, Throwable t) {
		log.error("CircuitBreaker fallback: {}", t.getMessage(), t);
		return CompletableFuture.failedFuture(t);
	}

	protected CompletableFuture<Response> timeoutFallback(CreateWebtoonApiRequest request, Throwable t) {
		log.error("TimeLimiter fallback: {}", t.getMessage(), t);
		return CompletableFuture.failedFuture(t);
	}

	// 헬스 체크에도 Retry 적용
	@Retry(name = "health-retry", fallbackMethod = "healthFallback")
	protected void checkAiServerStatusWithRetry() {
		Response result = aiServerApi.healthCheck();
		if (result.status() != 200) {
			throw new ImageGenerationException(EXTERN_SERVER_HEALTH_CHECK_FAIL);
		}
		log.info("Ai Server Health Check OK");
	}

	protected void healthFallback(Throwable t) {
		log.error("AI 서버 헬스체크 실패 - fallback", t);
		throw new ImageGenerationException(EXTERN_SERVER_HEALTH_CHECK_FAIL);
	}
}
