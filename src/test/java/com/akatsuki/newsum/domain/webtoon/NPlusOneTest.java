package com.akatsuki.newsum.domain.webtoon;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Category;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import jakarta.transaction.Transactional;

//통합테스트를 위한 class
@SpringBootTest
@Transactional
class NPlusOneTest {

	@Autowired
	WebtoonRepository webtoonRepository;

	@Test
	void nPlusOne_확인() {
		List<Webtoon> webtoons = webtoonRepository.findWebtoonByCategory(Category.IT);
		for (Webtoon webtoon : webtoons) {
			System.out.println(webtoon.getAiAuthor().getName());
		}
	}
}

