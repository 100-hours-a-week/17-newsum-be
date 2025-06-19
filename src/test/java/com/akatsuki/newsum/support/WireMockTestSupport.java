package com.akatsuki.newsum.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public abstract class WireMockTestSupport extends TestContainerSupport {

	@BeforeEach
	void resetWireMock() {
		WireMock.reset();
	}

	protected void verifyPostCalledWithBodyContaining(String url, String expectedContent) {
		WireMock.verify(
			WireMock.postRequestedFor(WireMock.urlEqualTo(url))
				.withRequestBody(WireMock.containing(expectedContent))
		);
	}

	protected void verifyGetCalled(String url) {
		WireMock.verify(
			WireMock.getRequestedFor(WireMock.urlEqualTo(url))
		);
	}
}
