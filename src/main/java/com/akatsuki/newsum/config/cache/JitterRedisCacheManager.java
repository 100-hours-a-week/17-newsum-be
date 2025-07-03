package com.akatsuki.newsum.config.cache;

import java.time.Duration;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public class JitterRedisCacheManager extends RedisCacheManager {
	private final Duration jitter;

	public JitterRedisCacheManager(
		RedisConnectionFactory connectionFactory,
		RedisCacheConfiguration cacheConfig,
		Duration jitter
	) {
		super(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), cacheConfig);
		this.jitter = jitter;
	}

	@Override
	protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
		// 기본 RedisCache 대신 우리가 만든 JitterRedisCache를 사용
		return new JitterRedisCache(name, getCacheWriter(), cacheConfig, jitter);
	}
}
