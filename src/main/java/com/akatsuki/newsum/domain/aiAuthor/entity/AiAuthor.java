package com.akatsuki.newsum.domain.aiAuthor.entity;

import java.util.ArrayList;
import java.util.List;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_author")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiAuthor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	private String name;

	@Column(nullable = false, length = 100)
	private String style;

	@Column(nullable = false, length = 100)
	private String introduction;

	@Column(name = "profile_image_url", nullable = false, length = 1000)
	private String profileImageUrl;

	@OneToMany(mappedBy = "aiAuthor", cascade = CascadeType.ALL)
	private List<Webtoon> webtoons = new ArrayList<>();

	public AiAuthor(String name, String style, String introduction, String profileImageUrl) {
		this.name = name;
		this.style = style;
		this.introduction = introduction;
		this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "BASIC_PROFILE_IMAGE_URL";
	}
}
