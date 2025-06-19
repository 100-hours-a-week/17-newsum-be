package com.akatsuki.newsum.job;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.akatsuki.newsum.cache.RedisService;
import com.akatsuki.newsum.domain.user.entity.User;
import com.akatsuki.newsum.domain.user.repository.UserRepository;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.Webtoon;
import com.akatsuki.newsum.domain.webtoon.entity.webtoon.WebtoonLike;
import com.akatsuki.newsum.domain.webtoon.exception.WebtoonNotFoundException;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonLikeRepository;
import com.akatsuki.newsum.domain.webtoon.repository.WebtoonRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebtoonLikeSyncJob {

	private final RedisService redisService;
	private final WebtoonRepository webtoonRepository;
	private final WebtoonLikeRepository webtoonLikeRepository;
	private final UserRepository userRepository;

	@Scheduled(cron = "0 0 0 * * *")
	public void syncLikesFromRedisToDB() {
		Set<String> keys = redisService.getKeys("webtoon:likes:*");

		for (String key : keys) {
			try {
				Long webtoonId = Long.parseLong(key.split(":")[2]);
				Set<Object> userIds = redisService.getSetMembers(key);

				Webtoon webtoon = webtoonRepository.findById(webtoonId)
					.orElseThrow(() -> new WebtoonNotFoundException());

				webtoon.updateLikeCount(userIds.size());
				webtoonRepository.save(webtoon);

				for (Object userIdObj : userIds) {
					try {
						Long userId = Long.valueOf(userIdObj.toString());
						User user = userRepository.findById(userId).orElse(null);
						if (user != null) {
							webtoonLikeRepository.save(new WebtoonLike(user, webtoon));
						}
					} catch (Exception e) {
						log.warn("⚠️ userId={} insert 실패: {}", userIdObj, e.getMessage());
					}
				}

			} catch (Exception e) {
				log.error("❌ 좋아요 반영 실패 - key={}, reason={}", key, e.getMessage(), e);
			}
		}
	}
}
