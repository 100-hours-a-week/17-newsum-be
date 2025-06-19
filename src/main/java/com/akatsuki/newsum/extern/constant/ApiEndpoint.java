package com.akatsuki.newsum.extern.constant;

import org.springframework.http.HttpMethod;

import lombok.Getter;

@Getter
public enum ApiEndpoint {

	CREATE_WEBTOON(HttpMethod.POST, "/api/v2/images/generate/batch"),
	HEALTH_CHECK(HttpMethod.GET, "/api/v2/images/health");

	private final HttpMethod httpMethod;
	private final String path;

	ApiEndpoint(HttpMethod httpMethod, String path) {
		this.httpMethod = httpMethod;
		this.path = path;
	}
}
