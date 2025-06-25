package com.akatsuki.newsum.domain.webtoon.entity.webtoon;

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
@Table(name = "news_source")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsSource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "webtoon_id", nullable = false)
	private Webtoon webtoon;

	@Column(nullable = false, length = 255)
	private String headline;

	@Column(nullable = false, length = 1000)
	private String url;

	public NewsSource(Webtoon webtoon, String headline, String url) {
		this.webtoon = webtoon;
		this.headline = headline;
		this.url = url;
	}

	public void setWebtoon(Webtoon webtoon) {
		this.webtoon = webtoon;
	}
}
