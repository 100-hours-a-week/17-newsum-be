package com.akatsuki.newsum.domain.webtoon.repository;

import java.util.List;
import java.util.Optional;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

public interface WebtoonQueryRepository {

	List<Webtoon> findWebtoonByCategoryWithCursor(Category category, Cursor cursor, int size);

	Optional<Webtoon> findWebtoonAndAiAuthorById(Long webtoonId);

	Optional<Webtoon> findWebtoonAndNewsSourceById(Long webtoonId);

	List<Webtoon> findTop3TodayByViewCount();

	List<Webtoon> findTodayNewsTop3();
}
