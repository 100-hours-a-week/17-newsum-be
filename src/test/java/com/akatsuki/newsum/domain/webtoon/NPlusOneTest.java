package com.akatsuki.newsum.domain.webtoon;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonLike;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonLikeRepository;

import jakarta.transaction.Transactional;

//통합테스트를 위한 class
@SpringBootTest
@Transactional
class NPlusOneTest {

	@Autowired
	WebtoonLikeRepository webtoonLikeRepository;

	@Test
	@DisplayName("웹툰좋아요에 대한 N+1 가져오기 ")
	void webtoonLike_NPlusOne_테스트() {
		List<WebtoonLike> likes = webtoonLikeRepository.findAll();

		for (WebtoonLike like : likes) {
			// 여기서 Lazy 로딩 트리거!
			// String title = like.getWebtoon().getTitle();
			System.out.println("웹툰 제목 = " + like.getWebtoon().getCreatedAt());
		}
	}
}

