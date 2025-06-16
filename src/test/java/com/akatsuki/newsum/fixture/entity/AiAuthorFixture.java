package com.akatsuki.newsum.fixture.entity;

import java.time.LocalDateTime;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;

public class AiAuthorFixture {

	public static final AiAuthor DEFAULT_AI_AUTHOR = new AiAuthor(
		"테스트 AI 작가",
		"유머러스한",
		"재미있는 웹툰을 그리는 AI 작가입니다.",
		"https://example.com/ai-author.jpg",
		LocalDateTime.now()
	);

	public static AiAuthor createAiAuthor() {
		return new AiAuthor(
			"테스트 AI 작가",
			"창의적인",
			"멋진 웹툰을 그리는 AI 작가입니다.",
			"https://example.com/ai-author.jpg",
			LocalDateTime.now()
		);
	}

	public static AiAuthor createAiAuthorWithName(String name) {
		return new AiAuthor(
			name,
			"유머러스한",
			"재미있는 웹툰을 그리는 AI 작가입니다.",
			"https://example.com/ai-author.jpg",
			LocalDateTime.now()
		);
	}
}
