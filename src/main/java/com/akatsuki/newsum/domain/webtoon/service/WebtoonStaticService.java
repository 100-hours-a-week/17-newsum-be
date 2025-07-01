package com.akatsuki.newsum.domain.webtoon.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.dto.AiAuthorInfoDto;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonSlideDto;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonStaticDto;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonDetail;
import com.akatsuki.newsum.domain.webtoon.exception.WebtoonNotFoundException;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 데이터 조회만 수행하므로 readOnly 트랜잭션 적용
public class WebtoonStaticService {

	private final WebtoonRepository webtoonRepository;

	/**
	 * 웹툰의 정적 정보를 조회하고 결과를 캐시에 저장합니다.
	 * 이 메서드는 외부 클래스(WebtoonService)에서 호출되므로 AOP 프록시가 정상 동작합니다.
	 */
	@Cacheable(value = "webtoon:static", key = "#webtoonId")
	public WebtoonStaticDto getCachedWebtoonStaticInfo(Long webtoonId) {
		log.info("캐시 없음! DB에서 웹툰 정적 정보를 조회합니다. webtoonId={}", webtoonId);

		Webtoon webtoon = findWebtoonWithAiAuthorByIdOrThrow(webtoonId);

		return new WebtoonStaticDto(
			webtoon.getId(),
			webtoon.getTitle(),
			webtoon.getThumbnailImageUrl(),
			mapWebToonSlides(webtoon),
			mapAiAuthorToAiAuthorInfoDto(webtoon.getAiAuthor()),
			webtoon.getViewCount(),
			webtoon.getCreatedAt()
		);
	}

	// --- Helper Methods (WebtoonService에서 그대로 옮겨온 private 메서드들) ---

	private Webtoon findWebtoonWithAiAuthorByIdOrThrow(Long webtoonId) {
		return webtoonRepository.findWebtoonAndAiAuthorById(webtoonId)
			.orElseThrow(WebtoonNotFoundException::new);
	}

	private List<WebtoonSlideDto> mapWebToonSlides(Webtoon webtoon) {
		return webtoon.getDetails().stream()
			.map(this::mapSingleDetailToWebtoonSlide)
			.sorted(Comparator.comparing(WebtoonSlideDto::slideSeq))
			.toList();
	}

	private WebtoonSlideDto mapSingleDetailToWebtoonSlide(WebtoonDetail webtoonDetail) {
		return new WebtoonSlideDto(webtoonDetail.getImageSeq(), webtoonDetail.getImageUrl(),
			webtoonDetail.getContent());
	}

	private AiAuthorInfoDto mapAiAuthorToAiAuthorInfoDto(AiAuthor aiAuthor) {
		return new AiAuthorInfoDto(aiAuthor.getId(), aiAuthor.getName(), aiAuthor.getProfileImageUrl());
	}
}
