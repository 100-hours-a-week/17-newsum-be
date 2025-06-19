package com.akatsuki.newsum.extern.service;

import static com.akatsuki.newsum.extern.constant.ApiEndpoint.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.akatsuki.newsum.extern.dto.CreateWebtoonApiRequest;
import com.akatsuki.newsum.fixture.extern.AiServerApiFixture;
import com.akatsuki.newsum.support.WireMockTestSupport;

import feign.Response;

class AiServerApiTest extends WireMockTestSupport {

	@Autowired
	private AiServerApi aiServerApi;

	@Test
	@DisplayName("웹툰 생성 API 요청이 올바른 형태로 전송되는지 테스트")
	void createWebtoon_shouldSendCorrectRequest() {
		// given
		CreateWebtoonApiRequest request = AiServerApiFixture.createBasicWebtoonRequest();

		// when
		Response response = aiServerApi.createWebtoon(request);

		// then
		assertThat(response.status()).isEqualTo(200);
		verifyCreateWebtoonCalledWithId(request.id());
	}

	@Test
	@DisplayName("헬스체크 API 요청이 올바른 형태로 전송되는지 테스트")
	void healthCheck_shouldSendCorrectRequest() {
		// when
		Response response = aiServerApi.healthCheck();

		// then
		assertThat(response.status()).isEqualTo(200);
		verifyHealthCheckCalled();
	}

	private void verifyCreateWebtoonCalledWithId(String id) {
		verifyPostCalledWithBodyContaining(CREATE_WEBTOON.getPath(), id);
	}

	private void verifyHealthCheckCalled() {
		verifyGetCalled(HEALTH_CHECK.getPath());
	}
}
