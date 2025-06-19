package com.akatsuki.newsum.domain.webtoon.service;

import static com.akatsuki.newsum.common.dto.ErrorCodeAndMessage.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.exception.NotFoundException;
import com.akatsuki.newsum.common.pagination.CursorPaginationService;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.page.CursorPage;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.aiAuthor.repository.AiAuthorRepository;
import com.akatsuki.newsum.domain.user.dto.KeywordListResponse;
import com.akatsuki.newsum.domain.user.dto.KeywordResponse;
import com.akatsuki.newsum.domain.user.entity.User;
import com.akatsuki.newsum.domain.user.repository.UserRepository;
import com.akatsuki.newsum.domain.user.service.KeywordService;
import com.akatsuki.newsum.domain.webtoon.dto.AiAuthorInfoDto;
import com.akatsuki.newsum.domain.webtoon.dto.CreateWebtoonReqeust;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonCardDto;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonDetailResponse;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonLikeStatusDto;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonResponse;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonSlideDto;
import com.akatsuki.newsum.domain.webtoon.dto.WebtoonSourceDto;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.GenerationStatus;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.ImageGenerationQueue;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.NewsSource;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.RecentView;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonDetail;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonFavorite;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonLike;
import com.akatsuki.newsum.domain.webtoon.exception.WebtoonNotFoundException;
import com.akatsuki.newsum.domain.webtoon.repository.ImageGenerationQueueRepository;
import com.akatsuki.newsum.domain.webtoon.repository.NewsSourceRepository;
import com.akatsuki.newsum.domain.webtoon.repository.RecentViewRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonDetailRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonFavoriteRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonLikeRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;
import com.akatsuki.newsum.extern.dto.ImageGenerationApiRequest;
import com.akatsuki.newsum.extern.dto.ImageGenerationCallbackRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WebtoonService {

	private final WebtoonRepository webtoonRepository;
	private final AiAuthorRepository aiAuthorRepository;
	private final WebtoonDetailRepository webtoonDetailRepository;
	private final NewsSourceRepository newsSourceRepository;
	private final RecentViewRepository recentViewRepository;
	private final UserRepository userRepository;
	private final WebtoonFavoriteRepository webtoonFavoriteRepository;
	private final WebtoonLikeRepository webtoonLikeRepository;
	private final CursorPaginationService cursorPaginationService;
	private final ImageGenerationQueueRepository imageGenerationQueueRepository;
	private final KeywordService keywordService;

	private final int RECENT_WEBTOON_LIMIT = 4;
	private final int RELATED_CATEGORY_SIZE = 2;
	private final int RELATED_AI_AUTHOR_SIZE = 2;
	private final int RELATED_NEWS_SIZE = RELATED_CATEGORY_SIZE + RELATED_AI_AUTHOR_SIZE;

	public List<WebtoonCardDto> findWebtoonsByCategory(String category, Cursor cursor, int size) {
		List<Webtoon> webtoons = webtoonRepository.findWebtoonByCategoryWithCursor(Category.valueOf(category), cursor,
			size);

		return webtoons.stream()
			.map(this::mapWebToonCardDto)
			.toList();
	}

	public WebtoonResponse getWebtoon(Long webtoonId, Long userId) {
		Webtoon webtoon = findWebtoonWithAiAuthorByIdOrThrow(webtoonId);

		WebtoonLikeStatusDto likeStatus = getWebtoonLikeStatus(webtoonId, userId);
		boolean isLiked = likeStatus.liked();
		long likeCount = likeStatus.likeCount();

		boolean isBookmarked = false;

		if (userId != null) {
			isBookmarked = webtoonFavoriteRepository.existsByWebtoonIdAndUserId(webtoonId, userId);

		}
		return new WebtoonResponse(
			webtoon.getId(),
			webtoon.getThumbnailImageUrl(),
			webtoon.getTitle(),
			mapWebToonSlides(webtoon),
			mapAiAuthorToAiAuthorInfoDto(webtoon.getAiAuthor()),
			isLiked,
			isBookmarked,
			likeCount,
			webtoon.getViewCount(),
			webtoon.getCreatedAt()
		);
	}

	@Transactional
	public void updateRecentView(Long webtoonId, Long userId) {
		if (userId == null) {
			return;
		}
		recentViewRepository.findByWebtoonIdAndUserId(webtoonId, userId)
			.ifPresentOrElse(RecentView::updateViewedAt,
				() -> {
					Webtoon webtoon = findWebtoonWithAiAuthorByIdOrThrow(webtoonId);
					User user = findUserById(userId);
					recentViewRepository.save(new RecentView(user, webtoon, LocalDateTime.now()));
				});
	}

	@Transactional
	public void updateViewCount(Long webtoonId) {
		Webtoon webtoon = findWebtoonById(webtoonId);
		webtoon.increaseViewCount();
	}

	public WebtoonDetailResponse getWebtoonDetail(Long webtoonId) {
		Webtoon webtoon = findWebtoonWithAiAuthorByIdOrThrow(webtoonId);

		List<WebtoonCardDto> relatedNews = fetchRelatedNews(webtoon);

		List<WebtoonSourceDto> sources = getNewsSources(webtoon);

		LocalDateTime createdAt = webtoon.getCreatedAt();

		Long commentCount = webtoon.getParentCommentCount();

		return new WebtoonDetailResponse(sources,
			relatedNews,
			createdAt,
			commentCount);
	}

	@Transactional
	public void createWebtoon(CreateWebtoonReqeust request) {
		AiAuthor aiAuthor = findAiAuthorById(request.aiAuthorId());

		Webtoon webtoon = new Webtoon(aiAuthor,
			Category.valueOf(request.category()),
			request.title(),
			request.content(),
			request.thumbnailImageUrl());

		Webtoon save = webtoonRepository.save(webtoon);

		List<NewsSource> newsSources = request.sourceNews().stream()
			.map(newsSourceDto -> new NewsSource(save, newsSourceDto.headline(), newsSourceDto.url()))
			.toList();

		List<WebtoonDetail> webtoonDetails = request.slides().stream()
			.map(webtoonSlideDto -> new WebtoonDetail(save, webtoonSlideDto.imageUrl(), webtoonSlideDto.content(),
				webtoonSlideDto.slideSeq()))
			.toList();

		webtoonDetailRepository.saveAll(webtoonDetails);
		newsSourceRepository.saveAll(newsSources);
	}

	public List<WebtoonCardDto> getTop3TodayByViewCount() {
		return webtoonRepository.findTop3TodayByViewCount().stream()
			.map(this::mapToCardDto)
			.toList();
	}

	public List<WebtoonCardDto> getTodayNewsCards() {
		return webtoonRepository.findTodayNewsTop3().stream()
			.map(this::mapToCardDto)
			.toList();
	}

	public Map<String, List<WebtoonCardDto>> getWebtoonsByCategoryLimit3() {
		Map<String, List<WebtoonCardDto>> result = new LinkedHashMap<>();
		for (Category category : Category.values()) {
			List<WebtoonCardDto> dtoList = webtoonRepository.findTop3ByCategoryOrderByCreatedAtDesc(category)
				.stream()
				.map(this::mapToCardDto)
				.toList();
			result.put(category.name(), dtoList);
		}
		return result;
	}

	public List<WebtoonCardDto> getRecentWebtoons(Long userId) {
		List<Webtoon> recentWebtoons = recentViewRepository.findRecentWebtoonsByUserId(userId, RECENT_WEBTOON_LIMIT);

		if (recentWebtoons.isEmpty()) {
			return List.of();
		}

		// 변환 중 오류 발생 시 명확하게 터뜨리는 것이 좋다.
		return recentWebtoons.stream()
			.map(this::mapToCardDto)
			.toList();
	}

	public List<WebtoonCardDto> searchWebtoons(String query, Cursor cursor, int size) {
		if (query == null || query.trim().isEmpty()) {
			throw new IllegalArgumentException("검색어는 비어있을 수 없습니다");
		}

		List<Webtoon> webtoons = webtoonRepository.searchByTitleContaining(query, cursor, size);
		return webtoons.stream()
			.map(this::mapToCardDto)
			.toList();
	}

	@Transactional
	public boolean toggleBookmark(Long webtoonId, Long userId) {
		Optional<WebtoonFavorite> favoriteOpt = webtoonFavoriteRepository
			.findByWebtoonIdAndUserId(webtoonId, userId);

		AtomicBoolean bookmarked = new AtomicBoolean(false);

		favoriteOpt.ifPresentOrElse(
			favorite -> {
				webtoonFavoriteRepository.delete(favorite);
				bookmarked.set(false);
			},
			() -> {
				User user = new User(userId);
				Webtoon webtoon = webtoonRepository.findById(webtoonId)
					.orElseThrow(() -> new BusinessException(WEBTOON_NOT_FOUND));
				webtoonFavoriteRepository.save(new WebtoonFavorite(user, webtoon));
				bookmarked.set(true);
			}
		);

		return bookmarked.get();

	}

	public CursorPage<WebtoonCardDto> getBookmarkedWebtoonCards(Long userId, Cursor cursor, int size) {
		List<WebtoonFavorite> favorites = webtoonFavoriteRepository
			.findFavoritesByUserIdWithCursor(userId, cursor, size);

		List<WebtoonCardDto> result = favorites.stream()
			.map(fav -> {
				Webtoon webtoon = fav.getWebtoon();
				return new WebtoonCardDto(
					webtoon.getId(),
					webtoon.getTitle(),
					webtoon.getThumbnailImageUrl(),
					fav.getCreatedAt(),
					webtoon.getViewCount()
				);
			})
			.toList();
		return cursorPaginationService.create(result, size, cursor);
	}

	@Transactional
	public boolean toggleWebtoonLike(Long webtoonId, Long userId) {
		Optional<WebtoonLike> likeOPt = webtoonLikeRepository
			.findByWebtoonIdAndUserId(webtoonId, userId);

		AtomicBoolean liked = new AtomicBoolean(false);

		likeOPt.ifPresentOrElse(
			webtoonLike -> {
				webtoonLikeRepository.delete(webtoonLike);
				liked.set(true);
			},

			() -> {
				User user = new User(userId);
				Webtoon webtoon = webtoonRepository.findById(webtoonId)
					.orElseThrow(() -> new BusinessException(WEBTOON_NOT_FOUND));
				webtoonLikeRepository.save(new WebtoonLike(user, webtoon));
				liked.set(true);
			}
		);

		return liked.get();
	}

	@Transactional
	public void saveimageprompts(ImageGenerationApiRequest request) {
		ImageGenerationQueue entity = ImageGenerationQueue.builder()
			.workId(request.workId())
			.aiAuthorId(request.aiAuthorId())
			.title(request.title())
			.content(request.content())
			.keyword(request.keyword())
			.category(request.category())
			.reportUrl(request.reportUrl())
			.description1(request.description1())
			.description2(request.description2())
			.description3(request.description3())
			.description4(request.description4())
			.imagePrompts(request.imagePrompts())
			.status(GenerationStatus.PENDING)
			.createdAt(LocalDateTime.now())
			.build();

		imageGenerationQueueRepository.save(entity);
	}

	@Transactional(readOnly = true)
	public WebtoonLikeStatusDto getWebtoonLikeStatus(Long webtoonId, Long userId) {
		boolean liked = webtoonLikeRepository.existsByWebtoonIdAndUserId(webtoonId, userId);
		long count = webtoonLikeRepository.countByWebtoonId(webtoonId);

		return new WebtoonLikeStatusDto(liked, count);
	}

	@Transactional
	public void imageGenerationCallbackRequest(ImageGenerationCallbackRequest request) {
		List<String> imageLinks = request.imageLinks();
		validateImageLinks(imageLinks);

		ImageGenerationQueue queue = findQueueById(request.requestId());
		List<String> descriptions = queue.getDescriptions();

		Webtoon webtoon = mapToWebtoonEntity(queue, parseThumbnailImage(imageLinks));
		List<WebtoonDetail> details = mapToWebtoonDetails(webtoon, descriptions, parseImageDescriptions(imageLinks));
		webtoonRepository.save(webtoon);
		webtoonDetailRepository.saveAll(details);
		queue.completed();
	}

	private String parseThumbnailImage(List<String> imageLinks) {
		return imageLinks.get(0);
	}

	private List<String> parseImageDescriptions(List<String> imageLinks) {
		return imageLinks.subList(1, imageLinks.size());
	}

	private void validateImageLinks(List<String> imageLinks) {
		if (imageLinks == null || imageLinks.isEmpty()) {
			throw new BusinessException(ErrorCodeAndMessage.INVALID_WEBTOON_IMAGE_URL);
		}
	}

	public List<WebtoonCardDto> findWebtoonsByUserKeywords(Long userId, Cursor cursor, int size) {
		KeywordListResponse keywordList = keywordService.getKeywordList(userId);

		String query = keywordList.keywords().stream()
			.map(KeywordResponse::content)
			.filter(content -> content != null && !content.trim().isEmpty())
			.map(content -> content.replaceAll("[:&|!]", ""))
			.collect(Collectors.joining(" | "));

		if (query.isBlank())
			return Collections.emptyList();

		List<Webtoon> webtoons = webtoonRepository.searchByUserKeywordBookmarks(query, cursor, size);

		return webtoons.stream()
			.map(this::mapToCardDto)
			.toList();
	}

	private AiAuthor findAiAuthorById(Long id) {
		return aiAuthorRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ErrorCodeAndMessage.AI_AUTHOR_NOT_FOUND));
	}

	private List<WebtoonCardDto> fetchRelatedNews(Webtoon webtoon) {
		List<Webtoon> webtoonByCategory = webtoonRepository.findWebtoonByCategory(webtoon.getCategory());
		List<Webtoon> webtoonByAiAuthor = webtoonRepository.findWebtoonByAiAuthor(webtoon.getAiAuthor());

		webtoonByCategory.remove(webtoon);
		webtoonByAiAuthor.remove(webtoon);

		webtoonByAiAuthor = removeDuplicateWebtoons(webtoonByAiAuthor, webtoonByCategory);

		int aiAuthorCount = Math.min(webtoonByAiAuthor.size(), RELATED_AI_AUTHOR_SIZE);
		int categoryCount = Math.min(webtoonByCategory.size(), RELATED_NEWS_SIZE - aiAuthorCount);

		Collections.shuffle(webtoonByCategory);
		Collections.shuffle(webtoonByAiAuthor);

		List<WebtoonCardDto> result = new ArrayList<>();
		result.addAll(webtoonByCategory.stream()
			.limit(categoryCount)
			.map(this::mapWebToonCardDto)
			.toList());

		result.addAll(webtoonByAiAuthor.stream()
			.limit(aiAuthorCount)
			.map(this::mapWebToonCardDto)
			.toList());

		return result;
	}

	private List<Webtoon> removeDuplicateWebtoons(List<Webtoon> targetWebtoons, List<Webtoon> toRemoveWebtoons) {
		Set<Webtoon> toRemoveWetoonSet = new HashSet<>(toRemoveWebtoons);

		return targetWebtoons.stream()
			.filter(w -> !toRemoveWetoonSet.contains(w))
			.collect(Collectors.toList());
	}

	private WebtoonCardDto mapWebToonCardDto(Webtoon webtoon) {
		return new WebtoonCardDto(webtoon.getId(), webtoon.getTitle(), webtoon.getThumbnailImageUrl(),
			webtoon.getCreatedAt(),
			webtoon.getViewCount());
	}

	private AiAuthorInfoDto mapAiAuthorToAiAuthorInfoDto(AiAuthor aiAuthor) {
		return new AiAuthorInfoDto(aiAuthor.getId(), aiAuthor.getName(), aiAuthor.getProfileImageUrl());
	}

	private List<WebtoonSlideDto> mapWebToonSlides(Webtoon webtoon) {
		return webtoon.getDetails().stream()
			.map(this::mapSingleDetialToWebtoonSlide)
			.sorted(Comparator.comparing(WebtoonSlideDto::slideSeq))
			.toList();
	}

	private WebtoonSlideDto mapSingleDetialToWebtoonSlide(WebtoonDetail webtoonDetail) {
		return new WebtoonSlideDto(webtoonDetail.getImageSeq(), webtoonDetail.getImageUrl(),
			webtoonDetail.getContent());
	}

	private List<WebtoonSourceDto> getNewsSources(Webtoon webtoon) {
		return webtoon.getNewsSources().stream()
			.map(this::mapSingWebtoonSourceDto)
			.toList();
	}

	private WebtoonSourceDto mapSingWebtoonSourceDto(NewsSource newsSource) {
		return new WebtoonSourceDto(newsSource.getHeadline(), newsSource.getUrl());
	}

	private Webtoon findWebtoonWithAiAuthorByIdOrThrow(Long webtoonId) {
		return webtoonRepository.findWebtoonAndAiAuthorById(webtoonId)
			.orElseThrow(WebtoonNotFoundException::new);
	}

	private Webtoon findWebtoonWithNewSourceById(Long webtoonId) {
		return webtoonRepository.findWebtoonAndNewsSourceById(webtoonId)
			.orElseThrow(WebtoonNotFoundException::new);
	}

	private Webtoon findWebtoonById(Long webtoonId) {
		return webtoonRepository.findById(webtoonId)
			.orElseThrow(WebtoonNotFoundException::new);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCodeAndMessage.USER_NOT_FOUND));
	}

	private WebtoonCardDto mapToCardDto(Webtoon webtoon) {
		return new WebtoonCardDto(
			webtoon.getId(),
			webtoon.getTitle(),
			webtoon.getThumbnailImageUrl(),
			webtoon.getCreatedAt(),
			webtoon.getViewCount()
		);
	}

	private ImageGenerationQueue findQueueById(Long requestId) {
		return imageGenerationQueueRepository.findById(requestId)
			.orElseThrow(() -> new NotFoundException(ErrorCodeAndMessage.IMAGE_GENERATION_QUEUE_NOT_FOUND));
	}

	private Webtoon mapToWebtoonEntity(ImageGenerationQueue queue, String thumbnailImageUrl) {
		AiAuthor aiAuthor = findAiAuthorById(queue.getAiAuthorId());

		return new Webtoon(
			aiAuthor,
			Category.from(queue.getCategory()),
			queue.getTitle(),
			queue.getContent(),
			thumbnailImageUrl
		);
	}

	private List<WebtoonDetail> mapToWebtoonDetails(Webtoon webtoon, List<String> descriptions,
		List<String> imageUrls) {
		List<WebtoonDetail> details = new ArrayList<>();

		for (int i = 0; i < imageUrls.size(); i++) {
			details.add(new WebtoonDetail(
				webtoon,
				imageUrls.get(i),
				descriptions.get(i),
				(byte)i // Byte 타입으로 seq 번호 캐스팅
			));
		}
		return details;
	}
}
