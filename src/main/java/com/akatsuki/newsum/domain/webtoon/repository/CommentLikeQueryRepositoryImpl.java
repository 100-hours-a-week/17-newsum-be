package com.akatsuki.newsum.domain.webtoon.repository;

import static com.akatsuki.newsum.domain.webtoon.entity.comment.entity.QCommentLike.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommentLikeQueryRepositoryImpl implements CommentLikeQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Set<Long> findLikedCommentIdsByUserIdAndCommentIds(Long userId, List<Long> commentIds) {
		if (userId == null) {
			return Collections.emptySet();
		}
		return queryFactory
			.select(commentLike.commentId)
			.from(commentLike)
			.where(
				commentLike.userId.eq(userId),
				commentLike.commentId.in(commentIds)
			)
			.fetch()
			.stream()
			.collect(Collectors.toSet());
	}
}
