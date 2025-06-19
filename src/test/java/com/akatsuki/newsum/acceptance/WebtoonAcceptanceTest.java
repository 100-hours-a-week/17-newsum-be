package com.akatsuki.newsum.acceptance;

import static com.akatsuki.newsum.acceptance.WebtoonStep.*;
import static com.akatsuki.newsum.fixture.dto.WebtoonDtoFixture.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.akatsuki.newsum.domain.webtoon.dto.CreateWebtoonReqeust;
import com.akatsuki.newsum.support.AcceptanceTestSupport;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("인수 : 웹툰")
public class WebtoonAcceptanceTest extends AcceptanceTestSupport {

	@Nested
	@Disabled
	@DisplayName("웹툰 생성")
	class WebtoonCreationAcceptanceTest {

		@Test
		@DisplayName("IT 카테고리 웹툰을 성공적으로 생성한다")
		void createItWebtoon_Success() {
			// given
			CreateWebtoonReqeust request = createItWebtoonRequest();

			// when
			var response = 웹툰_생성_요청(request);

			// then
			웹툰_생성_성공_검증(response);
		}

		@Test
		@DisplayName("금융 카테고리 웹툰을 성공적으로 생성한다")
		void createFinanceWebtoon_Success() {
			// given
			CreateWebtoonReqeust request = createFinanceWebtoonRequest();

			// when
			var response = 웹툰_생성_요청(request);

			// then
			웹툰_생성_성공_검증(response);
		}

		@Test
		@DisplayName("정치 카테고리 웹툰을 성공적으로 생성한다")
		void createPoliticsWebtoon_Success() {
			// given
			CreateWebtoonReqeust request = createPoliticsWebtoonRequest();

			// when
			var response = 웹툰_생성_요청(request);

			// then
			웹툰_생성_성공_검증(response);
		}
	}

	@Nested
	@DisplayName("웹툰 목록 조회")
	class WebtoonListAcceptanceTest {

		@Test
		@DisplayName("IT 카테고리의 웹툰 목록을 성공적으로 조회한다")
		void getItCategoryWebtoons_Success() {
			// when
			var response = 웹툰_목록_조회_요청(IT_CATEGORY, 10);

			// then
			웹툰_목록_조회_성공_검증(response);
		}

		@Test
		@DisplayName("금융 카테고리의 웹툰 목록을 성공적으로 조회한다")
		void getFinanceCategoryWebtoons_Success() {
			// when
			var response = 웹툰_목록_조회_요청(FINANCE_CATEGORY, 10);

			// then
			웹툰_목록_조회_성공_검증(response);
		}
	}

	@Nested
	@DisplayName("웹툰 상세 조회")
	class WebtoonDetailAcceptanceTest {

		@Test
		@DisplayName("존재하는 웹툰의 기본 정보를 성공적으로 조회한다")
		void getWebtoonBasicInfo_Success() {
			// when
			var response = 웹툰_기본정보_조회_요청(EXISTING_WEBTOON_ID);

			// then
			웹툰_기본정보_조회_성공_검증(response, EXISTING_WEBTOON_ID);
		}

		@Test
		@DisplayName("존재하는 웹툰의 상세 정보를 성공적으로 조회한다")
		void getWebtoonDetailInfo_Success() {
			// when
			var response = 웹툰_상세정보_조회_요청(EXISTING_WEBTOON_ID);

			// then
			웹툰_상세정보_조회_성공_검증(response);
		}

		@Test
		@DisplayName("존재하지 않는 웹툰 조회 시 404 에러가 발생한다")
		void getWebtoonBasicInfo_NotFound() {
			// when
			var response = 웹툰_기본정보_조회_요청(NON_EXISTENT_WEBTOON_ID);

			// then
			웹툰_조회_실패_검증(response);
		}
	}

	@Nested
	@DisplayName("웹툰 검색")
	class WebtoonSearchAcceptanceTest {

		@Test
		@DisplayName("유효한 검색어로 웹툰을 성공적으로 검색한다")
		void searchWebtoons_Success() {
			// when
			var response = 웹툰_검색_요청(VALID_SEARCH_QUERY, 10);

			// then
			웹툰_검색_성공_검증(response, VALID_SEARCH_QUERY);
		}

		@Test
		@DisplayName("존재하지 않는 검색어로 검색 시 빈 결과를 반환한다")
		void searchWebtoons_EmptyResult() {
			// when
			var response = 웹툰_검색_요청(EMPTY_SEARCH_QUERY, 10);

			// then
			웹툰_검색_빈결과_검증(response);
		}
	}

	@Nested
	@DisplayName("메인 페이지 데이터 조회")
	class MainPageDataAcceptanceTest {

		@Test
		@DisplayName("오늘의 인기 웹툰과 뉴스를 성공적으로 조회한다")
		void getTopWebtoonsAndNews_Success() {
			// when
			var response = 인기_웹툰_뉴스_조회_요청();

			// then
			인기_웹툰_뉴스_조회_성공_검증(response);
		}

		@Test
		@DisplayName("카테고리별 웹툰 메인 데이터를 성공적으로 조회한다")
		void getCategoryMainData_Success() {
			// when
			var response = 카테고리별_메인_데이터_조회_요청();

			// then
			카테고리별_메인_데이터_조회_성공_검증(response);
		}
	}

	@Nested
	@DisplayName("조회 페이지네이션 검증")
	class PaginationAcceptanceTest {

		@Test
		@DisplayName("카테고리별 웹툰 조회 시 커서 기반 페이지네이션이 정상적으로 동작한다")
		void pagination_CursorBased_Success() {
			// when - 첫 번째 페이지 조회
			var firstPageResponse = 페이지네이션_첫페이지_조회_요청("IT", 2);

			// then - 첫 번째 페이지 검증
			웹툰_목록_조회_성공_검증(firstPageResponse);

			// 다음 페이지가 있는 경우 커서 확인
			String nextCursor = firstPageResponse.jsonPath().getString("data.pageInfo.nextCursor");
			Boolean hasNext = firstPageResponse.jsonPath().getBoolean("data.pageInfo.hasNext");

			if (hasNext != null && hasNext && nextCursor != null) {
				// when - 다음 페이지 조회
				var secondPageResponse = 페이지네이션_다음페이지_조회_요청("IT", 2, nextCursor);

				// then - 페이지네이션 동작 검증
				웹툰_페이지네이션_동작_검증(firstPageResponse, secondPageResponse);
			}
		}
	}

	@Nested
	@Disabled
	@DisplayName("웹툰 통합 시나리오")
	class WebtoonIntegrationScenarioTest {

		/**
		 * Given : AI 작가가 등록되어 있다
		 * When : 웹툰 생성부터 조회까지의 전체 플로우를 수행한다
		 * Then : 정상적으로 동작한다
		 */
		@Test
		@DisplayName("웹툰 생성부터 조회까지의 전체 플로우가 정상적으로 동작한다")
		void webtoonFullFlow_Success() {
			// 1. 웹툰 생성
			CreateWebtoonReqeust createRequest = createItWebtoonRequest();
			ExtractableResponse<Response> createResponse = 웹툰_생성_요청(createRequest);
			웹툰_생성_성공_검증(createResponse);

			// 2. IT 카테고리 웹툰 목록에서 생성된 웹툰 확인
			ExtractableResponse<Response> listResponse = 웹툰_목록_조회_요청("IT", 20);
			웹툰_목록_조회_성공_검증(listResponse);

			// 3. 생성된 웹툰을 검색으로 찾기
			ExtractableResponse<Response> searchResponse = 웹툰_검색_요청("새로운", 10);
			웹툰_검색_성공_검증(searchResponse, "새로운");
		}
	}
}
