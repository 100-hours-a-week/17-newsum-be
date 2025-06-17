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
import com.akatsuki.newsum.domain.aiAuthor.dto.AiAuthorBookmarkedResponse;
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

	public AiAuthorListResponse getAuthorList(Long userId) {
		List<AiAuthor> authors = aiAuthorRepository.findAll();
		return buildAuthorListWithSubscribeStatus(userId, authors);
	}

	public List<AiAuthorBookmarkedResponse> getBookmarkedAuthor(Long userId) {
		List<AuthorFavorite> authors = findAuthorFavoritebyuserId(userId);
		return buildAuthorBookmarkedResponse(authors);
	}

	private AiAuthor findAuthorById(Long aiAuthorId) {
		return aiAuthorRepository.findById(aiAuthorId)
			.orElseThrow(() -> new BusinessException(ErrorCodeAndMessage.AI_AUTHOR_NOT_FOUND));
	}

	private Optional<AuthorFavorite> findFavorite(Long userId, Long aiAuthorId) {
		return aiAuthorFavoriteRepository.findByUserIdAndAiAuthorId(userId, aiAuthorId);
	}

	//유저아이디 기반으로 구독한 작가목록들 가져오기
	private List<AuthorFavorite> findAuthorFavoritebyuserId(Long userId) {
		return aiAuthorFavoriteRepository.findAiAuthorsByUserId(userId);
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

	private Set<Long> getSubscribedAuthorIds(Long userId, List<Long> authorIds) {
		if (userId == null) {
			return Set.of();
		}
		return aiAuthorQueryRepository.findSubscribedAuthorIdsByUserId(userId, authorIds);
	}

	private AiAuthorListResponse buildAuthorListWithSubscribeStatus(
		Long userId,
		List<AiAuthor> authors
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

		return new AiAuthorListResponse(items);
	}

	private List<AiAuthorBookmarkedResponse> buildAuthorBookmarkedResponse(List<AuthorFavorite> authors) {
		List<AiAuthorBookmarkedResponse> authorBookmarkedResponsesResponses = authors.stream()
			.map(AuthorFavorite::getAiAuthor)
			.map(author -> {
				List<AiAuthorWebtoonResponse> webtoons = author.getWebtoons().stream()
					.sorted(Comparator.comparing(Webtoon::getCreatedAt).reversed())
					.limit(4)
					.map(this::mapToWebtoonResponse)
					.toList();

				return new AiAuthorBookmarkedResponse(
					author.getId(),
					author.getName(),
					author.getProfileImageUrl(),
					webtoons
				);
			})
			.toList();
		return authorBookmarkedResponsesResponses;
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
