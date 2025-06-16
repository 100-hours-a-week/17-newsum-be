package com.akatsuki.newsum.domain.aiAuthor.repository;

import static com.akatsuki.newsum.domain.aiAuthor.entity.QAiAuthor.*;
import static com.akatsuki.newsum.domain.user.entity.QAuthorFavorite.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.akatsuki.newsum.common.pagination.model.cursor.Cursor;
import com.akatsuki.newsum.common.pagination.model.query.QueryEngineType;
import com.akatsuki.newsum.common.pagination.model.query.QueryFragment;
import com.akatsuki.newsum.common.pagination.query.registry.CursorPageQueryRegistry;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.aiAuthor.entity.QAiAuthor;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AiAuthorQueryRepositoryImpl implements AiAuthorQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final CursorPageQueryRegistry cursorPageQueryRegistry;

	@Override
	public List<AiAuthor> findByCursorAndSize(Cursor cursor, int size) {
		QueryFragment fragment = cursorPageQueryRegistry.resolve(
			QAiAuthor.class,
			cursor,
			QueryEngineType.QUERYDSL
		).buildQueryFragment(cursor);

		return queryFactory
			.selectFrom(aiAuthor)
			.where((Predicate)fragment.whereClause())
			.orderBy((OrderSpecifier<?>[])fragment.orderByClause())
			.limit(size + 1)
			.fetch();
	}

	@Override
	public Set<Long> findSubscribedAuthorIdsByUserId(Long userId, List<Long> aiAuthorIds) {
		return queryFactory
			.select(authorFavorite.aiAuthor.id)
			.from(authorFavorite)
			.where(
				authorFavorite.userId.eq(userId),
				authorFavorite.aiAuthor.id.in(aiAuthorIds)
			)
			.fetch()
			.stream()
			.collect(Collectors.toSet());
	}
}
