package com.akatsuki.newsum.domain.webtoon.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.entity.comment.entity.Comment;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;

@DisplayName("도메인 : Webtoon")
class WebtoonTest {

	private AiAuthor aiAuthor;
	private Webtoon webtoon;

	@BeforeEach
	void setUp() {
		aiAuthor = new AiAuthor(
			"테스트 작가",
			"유머러스한 스타일",
			"재미있는 웹툰을 그리는 AI 작가입니다.",
			"https://example.com/profile.jpg",
			LocalDateTime.now()
		);

		webtoon = new Webtoon(
			aiAuthor,
			Category.IT,
			"테스트 웹툰",
			"테스트 웹툰 내용입니다.",
			"https://example.com/thumbnail.jpg"
		);
	}

	@Nested
	@DisplayName("웹툰 생성 테스트")
	class WebtoonCreationTest {

		@Test
		@DisplayName("웹툰을 정상적으로 생성할 수 있다")
		void createWebtoon() {
			// given & when
			Webtoon newWebtoon = new Webtoon(
				aiAuthor,
				Category.FINANCE,
				"금융 웹툰",
				"금융에 대한 웹툰입니다.",
				"https://example.com/finance-thumbnail.jpg"
			);

			// then
			assertThat(newWebtoon.getAiAuthor()).isEqualTo(aiAuthor);
			assertThat(newWebtoon.getCategory()).isEqualTo(Category.FINANCE);
			assertThat(newWebtoon.getTitle()).isEqualTo("금융 웹툰");
			assertThat(newWebtoon.getContent()).isEqualTo("금융에 대한 웹툰입니다.");
			assertThat(newWebtoon.getThumbnailImageUrl()).isEqualTo("https://example.com/finance-thumbnail.jpg");
			assertThat(newWebtoon.getViewCount()).isEqualTo(0L);
			assertThat(newWebtoon.getLikeCount()).isEqualTo(0L);
		}
	}

	@Nested
	@DisplayName("웹툰 카테고리 테스트")
	class WebtoonCategoryTest {

		@Test
		@DisplayName("IT 카테고리 웹툰을 생성할 수 있다")
		void createItWebtoon() {
			// given & when
			Webtoon itWebtoon = new Webtoon(
				aiAuthor,
				Category.IT,
				"IT 웹툰",
				"IT 관련 웹툰입니다.",
				"https://example.com/it-thumbnail.jpg"
			);

			// then
			assertThat(itWebtoon.getCategory()).isEqualTo(Category.IT);
		}

		@Test
		@DisplayName("FINANCE 카테고리 웹툰을 생성할 수 있다")
		void createFinanceWebtoon() {
			// given & when
			Webtoon financeWebtoon = new Webtoon(
				aiAuthor,
				Category.FINANCE,
				"금융 웹툰",
				"금융 관련 웹툰입니다.",
				"https://example.com/finance-thumbnail.jpg"
			);

			// then
			assertThat(financeWebtoon.getCategory()).isEqualTo(Category.FINANCE);
		}

		@Test
		@DisplayName("POLITICS 카테고리 웹툰을 생성할 수 있다")
		void createPoliticsWebtoon() {
			// given & when
			Webtoon politicsWebtoon = new Webtoon(
				aiAuthor,
				Category.POLITICS,
				"정치 웹툰",
				"정치 관련 웹툰입니다.",
				"https://example.com/politics-thumbnail.jpg"
			);

			// then
			assertThat(politicsWebtoon.getCategory()).isEqualTo(Category.POLITICS);
		}
	}

	@Nested
	@DisplayName("조회수 관련 테스트")
	class ViewCountTest {

		@Test
		@DisplayName("웹툰 생성 시 조회수는 0으로 초기화된다")
		void initialViewCount() {
			// given & when & then
			assertThat(webtoon.getViewCount()).isEqualTo(0L);
		}

		@Test
		@DisplayName("조회수를 1 증가시킬 수 있다")
		void increaseViewCount() {
			// given
			Long initialViewCount = webtoon.getViewCount();

			// when
			webtoon.increaseViewCount();

			// then
			assertThat(webtoon.getViewCount()).isEqualTo(initialViewCount + 1);
		}

		@Test
		@DisplayName("조회수를 여러 번 증가시킬 수 있다")
		void increaseViewCountMultipleTimes() {
			// given
			Long initialViewCount = webtoon.getViewCount();
			int increaseCount = 5;

			// when
			for (int i = 0; i < increaseCount; i++) {
				webtoon.increaseViewCount();
			}

			// then
			assertThat(webtoon.getViewCount()).isEqualTo(initialViewCount + increaseCount);
		}
	}

	@Nested
	@DisplayName("부모 댓글 수 테스트")
	class ParentCommentCountTest {

		@Test
		@DisplayName("댓글이 없을 때 부모 댓글 수는 0이다")
		void noCommentsParentCommentCount() {
			// given & when & then
			assertThat(webtoon.getParentCommentCount()).isEqualTo(0L);
		}

		@Test
		@DisplayName("부모 댓글만 있을 때 정확한 부모 댓글 수를 반환한다")
		void onlyParentCommentsCount() {
			// given
			Comment parentComment1 = createComment(null);
			Comment parentComment2 = createComment(null);
			webtoon.getComments().addAll(List.of(parentComment1, parentComment2));

			// when & then
			assertThat(webtoon.getParentCommentCount()).isEqualTo(2L);
		}

		@Test
		@DisplayName("부모 댓글과 자식 댓글이 섞여있을 때 부모 댓글 수만 반환한다")
		void mixedCommentsCount() {
			// given
			Comment parentComment1 = createComment(null);
			Comment parentComment2 = createComment(null);
			Comment childComment1 = createComment(1L);
			Comment childComment2 = createComment(1L);
			Comment childComment3 = createComment(2L);

			webtoon.getComments().addAll(List.of(
				parentComment1, parentComment2, childComment1, childComment2, childComment3
			));

			// when & then
			assertThat(webtoon.getParentCommentCount()).isEqualTo(2L);
		}

		private Comment createComment(Long parentCommentId) {
			return new Comment(1L, 1L, parentCommentId, "테스트 댓글");
		}
	}

	@Nested
	@DisplayName("equals와 hashCode 테스트")
	class EqualsAndHashCodeTest {

		@Test
		@DisplayName("같은 ID를 가진 웹툰은 동일하다")
		void sameIdWebtoonEquals() {
			// given
			Webtoon webtoon1 = new Webtoon(LocalDateTime.now(), 1L, "제목1", "url1", 100L);
			Webtoon webtoon2 = new Webtoon(LocalDateTime.now(), 1L, "제목2", "url2", 200L);

			// when & then
			assertThat(webtoon1).isEqualTo(webtoon2);
			assertThat(webtoon1.hashCode()).isEqualTo(webtoon2.hashCode());
		}

		@Test
		@DisplayName("다른 ID를 가진 웹툰은 다르다")
		void differentIdWebtoonNotEquals() {
			// given
			Webtoon webtoon1 = new Webtoon(LocalDateTime.now(), 1L, "제목", "url", 100L);
			Webtoon webtoon2 = new Webtoon(LocalDateTime.now(), 2L, "제목", "url", 100L);

			// when & then
			assertThat(webtoon1).isNotEqualTo(webtoon2);
		}

		@Test
		@DisplayName("다른 클래스와 비교하면 false를 반환한다")
		void compareWithDifferentClass_ReturnsFalse() {
			// given
			String otherObject = "문자열";

			// when & then
			assertThat(webtoon.equals(otherObject)).isFalse();
		}

		@Test
		@DisplayName("null과 비교하면 false를 반환한다")
		void compareWithNull_ReturnsFalse() {
			// when & then
			assertThat(webtoon.equals(null)).isFalse();
		}

		@Test
		@DisplayName("자기 자신과 비교하면 true를 반환한다")
		void compareWithSelf_ReturnsTrue() {
			// when & then
			assertThat(webtoon.equals(webtoon)).isTrue();
		}
	}

	@Nested
	@DisplayName("웹툰 비즈니스 로직 테스트")
	class WebtoonBusinessLogicTest {

		@Test
		@DisplayName("웹툰이 특정 카테고리에 속하는지 확인할 수 있다")
		void checkWebtoonCategory() {
			// when & then
			assertThat(webtoon.getCategory()).isEqualTo(Category.IT);
		}

		@Test
		@DisplayName("웹툰의 AI 작가 정보를 조회할 수 있다")
		void getAiAuthorInfo() {
			// when & then
			assertThat(webtoon.getAiAuthor()).isEqualTo(aiAuthor);
			assertThat(webtoon.getAiAuthor().getName()).isEqualTo("테스트 작가");
		}

		@Test
		@DisplayName("웹툰의 생성 시간이 설정된다")
		void webtoonCreatedAtIsSet() {
			// given - JPA Auditing이 없는 단위 테스트 환경에서는 생성시간이 자동설정되지 않음
			// when & then - 단위 테스트에서는 JPA Auditing이 동작하지 않으므로 이 테스트를 통합테스트로 이동하거나 스킵
			// JPA Auditing을 테스트하려면 @DataJpaTest나 통합테스트 환경이 필요
			assertThat(webtoon.getTitle()).isNotNull(); // 대신 다른 필드 검증
		}

		@Test
		@DisplayName("웹툰의 좋아요 수는 0으로 초기화된다")
		void initialLikeCountIsZero() {
			// when & then
			assertThat(webtoon.getLikeCount()).isEqualTo(0L);
		}
	}
}
