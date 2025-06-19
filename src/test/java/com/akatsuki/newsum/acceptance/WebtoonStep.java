package com.akatsuki.newsum.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.akatsuki.newsum.domain.webtoon.dto.CreateWebtoonReqeust;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class WebtoonStep {

	// ========== 웹툰 생성 관련 Step ==========

	public static ExtractableResponse<Response> 웹툰_생성_요청(CreateWebtoonReqeust request) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.body(request)
			.when().post("/api/v1/webtoons")
			.then().log().all()
			.extract();
	}

	public static void 웹툰_생성_성공_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(201),
			() -> assertThat(response.jsonPath().getString("message")).isEqualTo("웹툰 생성에 성공했습니다.")
		);
	}

	// ========== 웹툰 목록 조회 관련 Step ==========

	public static ExtractableResponse<Response> 웹툰_목록_조회_요청(String category, int size) {
		return RestAssured
			.given().log().all()
			.param("category", category)
			.param("size", size)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons")
			.then().log().all()
			.extract();
	}

	public static void 웹툰_목록_조회_성공_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200),
			() -> assertThat(response.jsonPath().getList("data.webtoons")).isNotEmpty()
		);
	}

	// ========== 웹툰 상세 조회 관련 Step ==========

	public static ExtractableResponse<Response> 웹툰_기본정보_조회_요청(Long webtoonId) {
		return RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons/{webtoonId}", webtoonId)
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 웹툰_상세정보_조회_요청(Long webtoonId) {
		return RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons/{webtoonId}/details", webtoonId)
			.then().log().all()
			.extract();
	}

	public static void 웹툰_기본정보_조회_성공_검증(ExtractableResponse<Response> response, Long expectedId) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200),
			() -> assertThat(response.jsonPath().getLong("data.id")).isEqualTo(expectedId),
			() -> assertThat(response.jsonPath().getString("data.title")).isNotEmpty(),
			() -> assertThat(response.jsonPath().getString("data.thumbnailImageUrl")).isNotEmpty(),
			() -> assertThat(response.jsonPath().getString("data.author")).isNotEmpty(),
			() -> assertThat(response.jsonPath().getList("data.slides")).isNotEmpty(),
			() -> assertThat(response.jsonPath().getBoolean("data.isBookmarked")).isEqualTo(false),
			() -> assertThat(response.jsonPath().getBoolean("data.isLiked")).isEqualTo(false),
			() -> assertThat(response.jsonPath().getLong("data.likeCount")).isNotNull(),
			() -> assertThat(response.jsonPath().getLong("data.viewCount")).isNotNull()
		);
	}

	public static void 웹툰_상세정보_조회_성공_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200),
			() -> assertThat(response.jsonPath().getList("data.sourceNews")).isNotEmpty(),
			() -> assertThat(response.jsonPath().getList("data.relatedNews")).isNotEmpty()
		);
	}

	public static void 웹툰_조회_실패_검증(ExtractableResponse<Response> response) {
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	// ========== 웹툰 검색 관련 Step ==========

	public static ExtractableResponse<Response> 웹툰_검색_요청(String query, int size) {
		return RestAssured
			.given().log().all()
			.param("q", query)
			.param("size", size)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons/search")
			.then().log().all()
			.extract();
	}

	public static void 웹툰_검색_성공_검증(ExtractableResponse<Response> response, String expectedQuery) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200)
		);
		웹툰_목록_조회_성공_검증(response);
	}

	public static void 웹툰_검색_빈결과_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200),
			() -> assertThat(response.jsonPath().getList("data.webtoons")).isEmpty()
		);
	}

	// ========== 메인 페이지 데이터 관련 Step ==========

	public static ExtractableResponse<Response> 인기_웹툰_뉴스_조회_요청() {
		return RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons/top")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 카테고리별_메인_데이터_조회_요청() {
		return RestAssured
			.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons/main")
			.then().log().all()
			.extract();
	}

	public static void 인기_웹툰_뉴스_조회_성공_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200)
		);

		List<Map<String, Object>> topToons = response.jsonPath().getList("data.top3News");
		List<Map<String, Object>> todaysNews = response.jsonPath().getList("data.todayNews");

		assertThat(topToons).hasSizeLessThanOrEqualTo(3);
		assertThat(todaysNews).hasSizeLessThanOrEqualTo(3);
	}

	public static void 카테고리별_메인_데이터_조회_성공_검증(ExtractableResponse<Response> response) {
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getLong("code")).isEqualTo(200)
		);

		List<Map<String, Object>> itWebtoons = response.jsonPath().getList("data.IT");
		List<Map<String, Object>> financeWebtoons = response.jsonPath().getList("data.FINANCE");
		List<Map<String, Object>> politicsWebtoons = response.jsonPath().getList("data.POLITICS");

		assertThat(itWebtoons).hasSizeLessThanOrEqualTo(3);
		assertThat(financeWebtoons).hasSizeLessThanOrEqualTo(3);
		assertThat(politicsWebtoons).hasSizeLessThanOrEqualTo(3);
	}

	// ========== 페이지네이션 관련 Step ==========

	public static ExtractableResponse<Response> 페이지네이션_첫페이지_조회_요청(String category, int size) {
		return RestAssured
			.given().log().all()
			.param("category", category)
			.param("size", size)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons")
			.then().log().all()
			.extract();
	}

	public static ExtractableResponse<Response> 페이지네이션_다음페이지_조회_요청(String category, int size, String cursor) {
		return RestAssured
			.given().log().all()
			.param("category", category)
			.param("size", size)
			.param("cursor", cursor)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/api/v1/webtoons")
			.then().log().all()
			.extract();
	}

	public static void 웹툰_페이지네이션_동작_검증(ExtractableResponse<Response> firstPageResponse,
		ExtractableResponse<Response> secondPageResponse) {
		// 첫 번째 페이지 검증
		assertThat(firstPageResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Map<String, Object>> firstPageWebtoons = firstPageResponse.jsonPath().getList("data.webtoons");

		// 두 번째 페이지 검증
		assertThat(secondPageResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Map<String, Object>> secondPageWebtoons = secondPageResponse.jsonPath().getList("data.webtoons");

		// 페이지별 웹툰이 다른지 확인
		if (!firstPageWebtoons.isEmpty() && !secondPageWebtoons.isEmpty()) {
			Long firstPageFirstId = ((Number)firstPageWebtoons.get(0).get("id")).longValue();
			Long secondPageFirstId = ((Number)secondPageWebtoons.get(0).get("id")).longValue();
			assertThat(firstPageFirstId).isNotEqualTo(secondPageFirstId);
		}
	}
}
