package com.akatsuki.newsum.domain.webtoon.repository;

import static com.akatsuki.newsum.domain.aiAuthor.entity.QAiAuthor.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QNewsSource.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QRecentView.*;
import static com.akatsuki.newsum.domain.webtoon.entity.webtoon.QWebtoon.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;

import com.akatsuki.newsum.common.pagination.model.cursor.CreatedAtIdCursor;
import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.query.QueryEngineType;
import com.akatsuki.newsum.common.pagination.model.query.QueryFragment;
import com.akatsuki.newsum.common.pagination.query.CursorPageQueryBuilder;
import com.akatsuki.newsum.common.pagination.query.registry.CursorPageQueryRegistry;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.QWebtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.RecentView;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebtoonQueryRepositoryImpl implements WebtoonQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final CursorPageQueryRegistry cursorQueryRegistry;
	private final JdbcTemplate jdbcTemplate;

	@Override
	public List<Webtoon> findWebtoonByCategoryWithCursor(Category category, Cursor cursor, int size) {
		QueryFragment queryFragment = buildQueryFragment(cursor);
		return queryFactory.selectFrom(webtoon)
			.where(webtoon.category.eq(category)
				.and((Predicate)queryFragment.whereClause()))
			.orderBy((OrderSpecifier<?>[])queryFragment.orderByClause())
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

	@Override
	public List<Webtoon> searchByTitleContaining(String query, Cursor cursor, int size) {
		CreatedAtIdCursor createdAtIdCursor = (CreatedAtIdCursor)cursor;

		final String sql = """
			   SELECT DISTINCT w.id,
					  w.title,
					  w.content,
					  w.created_at,
					  w.thumbnail_image_url,
					  w.view_count
			   FROM webtoon w
			   LEFT JOIN webtoon_detail d ON w.id = d.webtoon_id
			   WHERE
			       to_tsvector('simple', coalesce(w.title, '') || ' ' || coalesce(w.content, '')) @@ plainto_tsquery('simple', ?)
			   OR
			       to_tsvector('simple', coalesce(d.content, '')) @@ plainto_tsquery('simple', ?)
				 AND (w.created_at < ? OR (w.created_at = ? AND w.id < ?))
			   ORDER BY w.created_at DESC, w.id DESC
			   LIMIT ?
			""";

		return jdbcTemplate.query(sql,
			new Object[] {
				query,
				query,
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getId(),
				size + 1
			},
			(rs, rowNum) ->
				new Webtoon(
					rs.getTimestamp("created_at").toLocalDateTime(),
					rs.getLong("id"),
					rs.getString("title"),
					rs.getString("thumbnail_image_url"),
					rs.getLong("view_count")
				)
		);
	}

	@Override
	public List<Webtoon> findTop3TodayByViewCount() {
		return queryFactory
			.selectFrom(webtoon)
			.orderBy(webtoon.viewCount.desc())
			.limit(3)
			.fetch();
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

	@Override
	public List<Webtoon> searchByUserKeywordBookmarks(String ftsQuery, Cursor cursor, int size) {
		CreatedAtIdCursor createdAtIdCursor = (CreatedAtIdCursor)cursor;

		final String sql = """
			SELECT w.id,
			       w.title,
			       w.content,
			       w.created_at,
			       w.thumbnail_image_url,
			       w.view_count
			FROM webtoon w
			WHERE to_tsvector('simple', coalesce(w.title, '') || ' ' || coalesce(w.content, '')) @@ to_tsquery('simple', ?)
			  AND (w.created_at < ? OR (w.created_at = ? AND w.id < ?))

			UNION

			SELECT w.id,
			       w.title,
			       w.content,
			       w.created_at,
			       w.thumbnail_image_url,
			       w.view_count
			FROM webtoon w
			JOIN (
				SELECT DISTINCT webtoon_id
				FROM webtoon_detail
				WHERE to_tsvector('simple', coalesce(content, '')) @@ to_tsquery('simple', ?)
			) d ON w.id = d.webtoon_id
			WHERE (w.created_at < ? OR (w.created_at = ? AND w.id < ?))

			ORDER BY created_at DESC, id DESC
			LIMIT ?
			""";

		return jdbcTemplate.query(sql,
			new Object[] {
				ftsQuery, // for webtoon
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getId(),

				ftsQuery, // for webtoon_detail
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getCreatedAt(),
				createdAtIdCursor.getId(),

				size + 1
			},
			(rs, rowNum) -> new Webtoon(
				rs.getTimestamp("created_at").toLocalDateTime(),
				rs.getLong("id"),
				rs.getString("title"),
				rs.getString("thumbnail_image_url"),
				rs.getLong("view_count")
			)
		);
	}

	private QueryFragment buildQueryFragment(Cursor cursor) {
		CursorPageQueryBuilder<Cursor> queryBuilder = cursorQueryRegistry.resolve(QWebtoon.class, cursor,
			QueryEngineType.QUERYDSL);
		QueryFragment queryFragment = queryBuilder.buildQueryFragment(cursor);
		return queryFragment;
	}
}
