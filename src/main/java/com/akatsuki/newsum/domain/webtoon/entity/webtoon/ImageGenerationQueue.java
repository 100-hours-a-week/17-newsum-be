package com.akatsuki.newsum.domain.webtoon.entity.webtoon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "image_generation_queue")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ImageGenerationQueue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 기본 정보
	@Column(name = "work_id")
	private String workId;

	@Column(name = "ai_author_id")
	private Long aiAuthorId;

	@Column(name = "title")
	private String title;

	@Column(name = "category")
	private String category;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "keyword", columnDefinition = "jsonb")
	private List<String> keyword;

	@Column(name = "report_url")
	private String reportUrl;

	private String content;

	@Column(name = "image_description_1")
	private String description1;

	@Column(name = "image_description_2")
	private String description2;

	@Column(name = "image_description_3")
	private String description3;

	@Column(name = "image_description_4")
	private String description4;

	// 이미지 및 대사
	@Column(name = "image_prompts", columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<Map<String, Object>> imagePrompts;

	// 상태 및 시각
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private GenerationStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	public void processing() {
		this.status = GenerationStatus.PROCESSING;
	}

	public void completed() {
		this.status = GenerationStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}

	public List<String> getDescriptions() {
		return List.of(description1, description2, description3, description4);
	}
}
