package com.akatsuki.newsum.domain.webtoon.entity.webtoon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.akatsuki.newsum.common.entity.BaseTimeEntity;
import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webtoon")
@Getter
@SQLDelete(sql = "UPDATE webtoon SET deleted_at = now() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Webtoon extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ai_author_id", nullable = false)
	private AiAuthor aiAuthor;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Category category;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 1023)
	private String content;

	@Column(name = "thumbnail_image_url", nullable = false, length = 1000)
	private String thumbnailImageUrl;

	@Column(name = "view_count", nullable = false)
	private Long viewCount = 0L;

	@Column(name = "like_count", nullable = false)
	private Long likeCount = 0L;

	private LocalDateTime deletedAt;

	@OneToMany(mappedBy = "webtoon")
	private List<WebtoonDetail> details = new ArrayList<>();

	@OneToMany(mappedBy = "webtoon")
	private List<NewsSource> newsSources = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "webtoon_id")
	private List<Comment> comments = new ArrayList<>();

	public Webtoon(AiAuthor aiAuthor, Category category, String title, String content, String thumbnailImageUrl) {
		this.aiAuthor = aiAuthor;
		this.category = category;
		this.title = title;
		this.content = content;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.viewCount = 0L;
		this.likeCount = 0L;
	}

	public Webtoon(LocalDateTime createdAt, Long id, String title, String thumbnailImageUrl, Long viewCount) {
		super(createdAt);
		this.id = id;
		this.title = title;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.viewCount = viewCount;
	}

	public Long getParentCommentCount() {
		return comments.stream()
			.filter(comment -> comment.getParentCommentId() == null)
			.count();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		Webtoon webtoon = (Webtoon)o;
		return Objects.equals(id, webtoon.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void updateLikeCount(long likeCount) {
		this.likeCount = likeCount;
	}

	public Webtoon(Long id) {
		this.id = id;
	}
}
