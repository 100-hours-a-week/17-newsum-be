package com.akatsuki.newsum.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

	Optional<Keyword> findByContent(String keywordContent);

}
