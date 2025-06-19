package com.akatsuki.newsum.domain.webtoon.entity.webtoon;

import java.time.LocalDateTime;

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
@Table(name = "keyword_favorite")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordFavorite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "keyword_id", nullable = false)
	private Keyword keyword;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public KeywordFavorite(Long userId, Keyword keyword) {
		this.userId = userId;
		this.keyword = keyword;
		this.createdAt = LocalDateTime.now();
	}
}
