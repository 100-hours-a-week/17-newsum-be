package com.akatsuki.newsum.domain.aiAuthor.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.pagination.CursorPaginationService;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.page.CursorPage;
import com.akatsuki.newsum.common.pagination.model.page.PageInfo;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorDetailResponse;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorListItemResponse;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorListResponse;
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorWebtoonResponse;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.aiAuthor.repository.AiAuthorQueryRepository;
import com.akatsuki.newsum.domain.aiAuthor.repository.AiAuthorRepository;
import com.akatsuki.newsum.domain.user.entity.AuthorFavorite;
import com.akatsuki.newsum.domain.user.repository.AiAuthorFavoriteRepository;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class AiAuthorService {

	private final AiAuthorFavoriteRepository aiAuthorFavoriteRepository;
	private final AiAuthorRepository aiAuthorRepository;
	private final AiAuthorQueryRepository aiAuthorQueryRepository;
	private final CursorPaginationService cursorPaginationService;

	public void toggleSubscribe(Long userId, Long aiAuthorId) {
		AiAuthor author = findAuthorById(aiAuthorId);
		findFavorite(userId, aiAuthorId).ifPresentOrElse(
			aiAuthorFavoriteRepository::delete,
			() -> aiAuthorFavoriteRepository.save(new AuthorFavorite(userId, author))
		);
	}

	@Transactional(readOnly = true)
	public AiAuthorDetailResponse getAuthorDetail(Long userId, Long aiAuthorId) {
		AiAuthor author = findAuthorById(aiAuthorId);
		List<AiAuthorWebtoonResponse> webtoons = getAuthorWebtoonsSortedByLatest(author);
		Boolean isSubscribed = isSubscribed(userId, aiAuthorId);

		return toDetailResponse(author, webtoons, isSubscribed);
	}

	public AiAuthorListResponse getAuthorList(Long userId, Cursor cursor, int size) {
		CursorPage<AiAuthor> page = findAiAuthors(cursor, size);
		return buildAuthorListWithSubscribeStatus(userId, page.getItems(), page.getPageInfo());
	}

	private AiAuthor findAuthorById(Long aiAuthorId) {
		return aiAuthorRepository.findById(aiAuthorId)
			.orElseThrow(() -> new BusinessException(ErrorCodeAndMessage.AI_AUTHOR_NOT_FOUND));
	}

	private Optional<AuthorFavorite> findFavorite(Long userId, Long aiAuthorId) {
		return aiAuthorFavoriteRepository.findByUserIdAndAiAuthorId(userId, aiAuthorId);
	}

	private AiAuthorDetailResponse toDetailResponse(AiAuthor author, List<AiAuthorWebtoonResponse> webtoons,
		Boolean isSubscribed) {
		return new AiAuthorDetailResponse(
			author.getId(),
			author.getName(),
			author.getStyle(),
			author.getIntroduction(),
			author.getProfileImageUrl(),
			webtoons,
			isSubscribed
		);
	}

	private AiAuthorWebtoonResponse mapToWebtoonResponse(Webtoon webtoon) {
		return new AiAuthorWebtoonResponse(
			webtoon.getId(),
			webtoon.getTitle(),
			webtoon.getThumbnailImageUrl()
		);
	}

	private List<AiAuthorWebtoonResponse> getAuthorWebtoonsSortedByLatest(AiAuthor author) {
		return author.getWebtoons().stream()
			.sorted(Comparator.comparing(Webtoon::getCreatedAt).reversed())
			.map(this::mapToWebtoonResponse)
			.toList();
	}

	private CursorPage<AiAuthor> findAiAuthors(Cursor cursor, int size) {
		List<AiAuthor> authors = aiAuthorQueryRepository.findByCursorAndSize(cursor, size);
		return cursorPaginationService.create(authors, size, cursor);
	}

	private Set<Long> getSubscribedAuthorIds(Long userId, List<Long> authorIds) {
		if (userId == null) {
			return Set.of();
		}
		return aiAuthorQueryRepository.findSubscribedAuthorIdsByUserId(userId, authorIds);
	}

	private AiAuthorListResponse buildAuthorListWithSubscribeStatus(
		Long userId,
		List<AiAuthor> authors,
		PageInfo pageInfo
	) {
		List<Long> authorIds = authors.stream()
			.map(AiAuthor::getId)
			.toList();

		Set<Long> subscribedIds = getSubscribedAuthorIds(userId, authorIds);

		List<AiAuthorListItemResponse> items = authors.stream()
			.sorted(Comparator.comparing(AiAuthor::getName))
			.map(author -> new AiAuthorListItemResponse(
				author.getId(),
				author.getName(),
				author.getProfileImageUrl(),
				subscribedIds.contains(author.getId())
			)).toList();

		return new AiAuthorListResponse(items, pageInfo);
	}

	private boolean isSubscribed(Long userId, Long aiAuthorId) {
		if (userId == null) {
			return false;
		}
		return aiAuthorFavoriteRepository
			.findByUserIdAndAiAuthorId(userId, aiAuthorId)
			.isPresent();
	}
}
