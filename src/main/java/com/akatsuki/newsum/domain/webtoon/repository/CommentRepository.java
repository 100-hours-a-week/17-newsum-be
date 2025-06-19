package com.akatsuki.newsum.domain.webtoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentQueryRepository {

	List<Comment> findByWebtoonId(Long webtoonId);

	Long countCommentsByWebtoonId(Long webtoonId);

	@Query("SELECT c.userId FROM Comment c WHERE c.id = :id")
	Long findCommentUserIdById(Long id);
}
