package com.akatsuki.newsum.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.domain.user.entity.AuthorFavorite;

public interface AiAuthorFavoriteRepository extends JpaRepository<AuthorFavorite, Long> {
	Optional<AuthorFavorite> findByUserIdAndAiAuthorId(Long userId, Long aiAuthorId);

	void deleteByUserIdAndAiAuthorId(Long userId, Long aiAuthorId);
}
