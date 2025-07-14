package com.akatsuki.newsum.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.annotation.PostConstruct;

@Component
public class ViewerEventDeduplicationCache {
	private Cache<String, Boolean> cache;

	@PostConstruct
	public void init() {
		this.cache = Caffeine.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.maximumSize(10_000)
			.build();
	}

	public boolean isDuplicate(String dedupKey) {
		if (cache.getIfPresent(dedupKey) != null) {
			return true;
		}
		cache.put(dedupKey, true);
		return false;
	}
}
