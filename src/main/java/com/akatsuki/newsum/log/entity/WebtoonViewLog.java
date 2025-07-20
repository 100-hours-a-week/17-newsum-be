package com.akatsuki.newsum.domain.log.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "webtoon_view_log")
public class WebtoonViewLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;

	private Long webtoonId;

	private LocalDateTime viewAt;

	protected WebtoonViewLog() {
	}

	public WebtoonViewLog(String userId, Long webtoonId, LocalDateTime viewAt) {
		this.userId = userId;
		this.webtoonId = webtoonId;
		this.viewAt = viewAt;
	}
}
