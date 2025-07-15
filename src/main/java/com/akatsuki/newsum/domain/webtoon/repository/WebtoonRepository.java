package com.akatsuki.newsum.domain.webtoon.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long>, WebtoonQueryRepository {

	List<Webtoon> findWebtoonByCategory(Category category);

	List<Webtoon> findWebtoonByAiAuthor(AiAuthor aiAuthor);

	List<Webtoon> findTop3ByCategoryOrderByCreatedAtDesc(Category category);

	List<Webtoon> searchByUserKeywordBookmarks(String ftsQuery, Cursor cursor, int size);

	@Query("SELECT w FROM Webtoon w WHERE w.id != :webtoonId AND w.category = :category ORDER BY w.viewCount DESC")
	List<Webtoon> findTop10WebtoonsByCategory(@Param("category") Category category, @Param("webtoonId") Long webtoonId,
		Pageable pageable);

	@Query("SELECT w FROM Webtoon w WHERE w.id != :webtoonId AND w.aiAuthor = :aiAuthor AND w.id NOT IN :excludeIds ORDER BY w.viewCount DESC")
	List<Webtoon> findTop10WebtoonsByAiAuthor(
		@Param("aiAuthor") AiAuthor aiAuthor,
		@Param("webtoonId") Long webtoonId,
		@Param("excludeIds") List<Long> excludeIds,
		Pageable pageable);
}
