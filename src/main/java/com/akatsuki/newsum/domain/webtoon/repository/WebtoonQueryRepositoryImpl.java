package com.akatsuki.newsum.domain.webtoon.repository;

import static com.akatsuki.newsum.domain.aiAuthor.entity.QAiAuthor.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QNewsSource.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QRecentView.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QWebtoon.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.akatsuki.newsum.common.pagination.model.cursor.CreatedAtIdCursor;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.querybuilder.CursorQueryBuilder;
import com.akatsuki.newsum.common.pagination.querybuilder.registry.CursorQueryRegistry;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.RecentView;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebtoonQueryRepositoryImpl implements WebtoonQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final CursorQueryRegistry cursorQueryRegistry;

	@Override
	public List<Webtoon> findWebtoonByCategoryWithCursor(Category category, Cursor cursor, int size) {
		//TODO : CursorQueryBuilder 수정 필요
		CursorQueryBuilder<Cursor> queryBuilder = cursorQueryRegistry.resolve(cursor);

		return queryFactory.selectFrom(webtoon)
			.where(webtoon.category.eq(category)
				.and(buildBooleanExpression(cursor)))
			.orderBy(webtoon.createdAt.asc())
			.orderBy(webtoon.createdAt.desc())
			.limit(size + 1)
			.fetch();
	}

	@Override
	public Optional<Webtoon> findWebtoonAndAiAuthorById(Long webtoonId) {
		return queryFactory
			.selectFrom(webtoon)
			.join(webtoon.aiAuthor, aiAuthor).fetchJoin()
			.where(webtoon.id.eq(webtoonId))
			.fetch()
			.stream().findFirst();
	}

	@Override
	public Optional<Webtoon> findWebtoonAndNewsSourceById(Long webtoonId) {
		return queryFactory
			.selectFrom(webtoon)
			.leftJoin(webtoon.newsSources, newsSource).fetchJoin()
			.where(webtoon.id.eq(webtoonId))
			.fetch()
			.stream().findFirst();
	}

	@Override
	public List<RecentView> findRecentWebtoons(Long id) {
		return queryFactory
			.selectFrom(recentView)
			.join(webtoon).fetchJoin()
			.on(webtoon.id.eq(recentView.webtoon.id))
			.where(recentView.user.id.eq(id))
			.orderBy(recentView.viewedAt.desc())
			.fetch();
	}

	private BooleanExpression buildBooleanExpression(Cursor cursor) {
		CreatedAtIdCursor createdAtIdCursor = (CreatedAtIdCursor)cursor;
		return webtoon.createdAt.gt(createdAtIdCursor.getCreatedAt())
			.or(webtoon.createdAt.eq(createdAtIdCursor.getCreatedAt()).and(webtoon.id.goe(createdAtIdCursor.getId())));
	}

	@Override
	public List<Webtoon> findTop3TodayByViewCount() {
		LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();

		List<Webtoon> result = new ArrayList<>();
		int maxDaysToLookBack = 10;
		int dayoffset = 0;

		while (result.size() < 3 && dayoffset < maxDaysToLookBack) {
			LocalDateTime start = startOfToday.minusDays(dayoffset);
			LocalDateTime end = start.plusDays(1);

			List<Webtoon> dailywebtoons = queryFactory
				.selectFrom(webtoon)
				.where(webtoon.createdAt.goe(start)
					.and(webtoon.createdAt.lt(end))
				)
				.orderBy(webtoon.viewCount.desc())
				.fetch();

			for (Webtoon webtoon : dailywebtoons) {
				if (result.size() >= 3)
					break;
				result.add(webtoon);
			}
			dayoffset++;

		}
		return result;
	}

	@Override
	public List<Webtoon> findTodayNewsTop3() {
		LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();

		return queryFactory
			.selectFrom(webtoon)
			.where(webtoon.createdAt.goe(startOfToday))
			.orderBy(webtoon.createdAt.desc())
			.limit(3)
			.fetch();
	}

}

