package com.akatsuki.newsum.domain.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.KeywordFavorite;

public interface KeywordFavoriteRepository extends JpaRepository<KeywordFavorite, Long> {
	List<KeywordFavorite> findByuserId(Long userid);

	Optional<KeywordFavorite> findByUserIdAndKeywordId(Long userId, Long keywordId);

	List<KeywordFavorite> findByUserId(Long userId);
}
