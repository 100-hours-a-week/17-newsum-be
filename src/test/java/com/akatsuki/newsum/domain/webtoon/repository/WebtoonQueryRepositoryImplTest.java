package com.akatsuki.newsum.domain.webtoon.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.akatsuki.newsum.domain.aiAuthor.entity.AiAuthor;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.QWebtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@ExtendWith(MockitoExtension.class)
class WebtoonQueryRepositoryImplTest {

	@Mock
	private JPAQueryFactory queryFactory;

	@Mock
	private JPAQuery<Webtoon> query;

	@InjectMocks
	private WebtoonQueryRepositoryImpl repository;

	@Test
	@DisplayName("오늘 웹툰이 3개 이상이면 그대로 반환")
	void todayWebtoonEnough() {
		// given
		List<Webtoon> todayWebtoons = List.of(
			createWebtoon("A"),
			createWebtoon("B"),
			createWebtoon("C")
		);

		// QueryDSL mocking 흐름
		given(queryFactory.selectFrom(QWebtoon.webtoon)).willReturn(query);
		given(query.where(any(Predicate.class))).willReturn(query);
		given(query.orderBy(any(OrderSpecifier.class))).willReturn(query);
		given(query.limit(anyLong())).willReturn(query);
		given(query.fetch()).willReturn(todayWebtoons);

		// when
		List<Webtoon> result = repository.findTodayNewsTop3();

		// then
		assertThat(result).hasSize(3);
		assertThat(result).extracting(Webtoon::getTitle).containsExactly("A", "B", "C");
	}

	private Webtoon createWebtoon(String title) {
		AiAuthor author = mock(AiAuthor.class);

		Webtoon webtoon = new Webtoon(
			author,
			Category.IT,
			title,
			"dummy content",
			"https://example.com/image.jpg"
		);

		ReflectionTestUtils.setField(webtoon, "createdAt", LocalDateTime.now());
		return webtoon;
	}

}
