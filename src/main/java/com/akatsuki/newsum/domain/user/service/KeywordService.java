package com.akatsuki.newsum.domain.user.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akatsuki.newsum.common.dto.ErrorCodeAndMessage;
import com.akatsuki.newsum.common.exception.BusinessException;
import com.akatsuki.newsum.common.exception.NotFoundException;
import com.akatsuki.newsum.domain.user.dto.KeywordListResponse;
import com.akatsuki.newsum.domain.user.dto.KeywordResponse;
import com.akatsuki.newsum.domain.user.entity.User;
import com.akatsuki.newsum.domain.user.repository.KeywordFavoriteRepository;
import com.akatsuki.newsum.domain.user.repository.KeywordRepository;
import com.akatsuki.newsum.domain.user.repository.UserRepository;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Keyword;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.KeywordFavorite;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {
	private final KeywordFavoriteRepository keywordFavoriteRepository;
	private final KeywordRepository keywordRepository;
	private final UserRepository userRepository;

	public void subscribeKeyword(Long userId, String keywordContent) {
		User user = findUserById(userId);
		Keyword keyword = keywordRepository.findByContent(keywordContent)
			.orElseGet(() -> keywordRepository.save(new Keyword(keywordContent)));

		try {
			KeywordFavorite favorite = user.subscribeKeyword(keyword);
			keywordFavoriteRepository.save(favorite);
		} catch (DataIntegrityViolationException e) {
			throw new BusinessException(ErrorCodeAndMessage.KEYWORD_ALREADY_SUBSCRIBED);
		}
	}

	public void unsubscribeKeyword(Long userId, Long keywordId) {
		KeywordFavorite favorite = keywordFavoriteRepository.findByUserIdAndKeywordId(userId, keywordId)
			.orElseThrow(() -> new BusinessException(ErrorCodeAndMessage.KEYWORD_SUBSCRIPTION_NOT_FOUND));
		keywordFavoriteRepository.delete(favorite);
	}

	public KeywordListResponse getKeywordList(Long userId) {
		List<KeywordFavorite> favorites = keywordFavoriteRepository.findByUserId(userId);

		List<KeywordResponse> keywordDtos = favorites.stream()
			.map(fav -> KeywordResponse.of(
				fav.getKeyword().getId(),
				fav.getKeyword().getContent(),
				fav.getCreatedAt()
			))
			.toList();

		return new KeywordListResponse(keywordDtos);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundException(ErrorCodeAndMessage.USER_NOT_FOUND));
	}
}
