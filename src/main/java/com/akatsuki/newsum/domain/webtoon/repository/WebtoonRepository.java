package com.akatsuki.newsum.domain.webtoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long>, WebtoonQueryRepository {

	List<Webtoon> findWebtoonByCategory(Category category);

	List<Webtoon> findWebtoonByAiAuthor(AiAuthor aiAuthor);

	List<Webtoon> findTop3ByCategoryOrderByCreatedAtDesc(Category category);

	List<Webtoon> searchByUserKeywordBookmarks(String ftsQuery, Cursor cursor, int size);

}
