package com.akatsuki.newsum.domain.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "author_favorite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorFavorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ai_author_id", nullable = false)
	private AiAuthor aiAuthor;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	public AuthorFavorite(Long userId, AiAuthor aiAuthor) {
		this.userId = userId;
		this.aiAuthor = aiAuthor;
	}
}
