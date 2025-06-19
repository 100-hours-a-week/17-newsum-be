package com.akatsuki.newsum.domain.webtoon.entity.comment.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.akatsuki.newsum.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@Getter
@SQLDelete(sql = "UPDATE comment SET deleted_at = now() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "webtoon_id", nullable = false)
	private Long webtoonId;

	@Column(name = "parent_comment_id")
	private Long parentCommentId;

	@Column(nullable = false, length = 400)
	private String content;

	@Column(name = "like_count", nullable = false)
	private Long likeCount = 0L;

	private LocalDateTime deletedAt;

	public void editComment(String content) {

		this.content = content;
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		this.likeCount--;
	}

	public Comment(Long userId, Long webtoonId, Long parentCommentId, String content) {
		this.userId = userId;
		this.webtoonId = webtoonId;
		this.parentCommentId = parentCommentId;
		this.content = content;
	}

	public boolean isParent() {
		return parentCommentId == null;
	}
}
