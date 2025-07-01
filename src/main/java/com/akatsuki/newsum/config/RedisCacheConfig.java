package com.akatsuki.newsum.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory cf) {
		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(cf)
			.cacheDefaults(redisCacheConfiguration())
			.build();
	}

	@Bean
	public RedisCacheConfiguration redisCacheConfiguration() {
		// ✅ ObjectMapper를 전역 Bean으로 등록하지 않고 여기서만 사용
		ObjectMapper redisMapper = new ObjectMapper();
		redisMapper.registerModule(new JavaTimeModule());
		redisMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		redisMapper.activateDefaultTyping(
			redisMapper.getPolymorphicTypeValidator(),
			ObjectMapper.DefaultTyping.EVERYTHING,
			JsonTypeInfo.As.WRAPPER_OBJECT
		);

		return RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new GenericJackson2JsonRedisSerializer(redisMapper)))
			.entryTtl(Duration.ofMinutes(5));

	}
}
