package com.akatsuki.newsum.config.cache;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class JitterRedisCache extends RedisCache {
	private final Duration jitter;

	public JitterRedisCache(
		String name,
		RedisCacheWriter cacheWriter,
		RedisCacheConfiguration cacheConfig,
		Duration jitter
	) {
		super(name, cacheWriter, cacheConfig);
		this.jitter = jitter;
	}

	@Override
	public void put(Object key, Object value) {
		Object cacheValue = preProcessCacheValue(value);
		if (cacheValue == null && !isAllowNullValues())
			return;

		byte[] binaryKey = serializeCacheKey(createCacheKey(key));
		byte[] binaryValue = serializeCacheValue(cacheValue);
		Duration ttl = getTtlWithJitter();

		getCacheWriter().put(getName(), binaryKey, binaryValue, ttl);
	}

	@Override
	public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
		Object cacheValue = preProcessCacheValue(value);
		if (cacheValue == null && !isAllowNullValues()) {
			return get(key);
		}

		byte[] binaryKey = serializeCacheKey(createCacheKey(key));
		byte[] binaryValue = serializeCacheValue(cacheValue);
		Duration ttl = getTtlWithJitter();

		byte[] result = getCacheWriter().putIfAbsent(getName(), binaryKey, binaryValue, ttl);
		if (result == null) {
			return null;
		}

		Object storeValue = fromStoreValue(deserializeCacheValue(result));
		return new SimpleValueWrapper(storeValue);
	}

	private Duration getTtlWithJitter() {
		Duration baseTtl = getCacheConfiguration().getTtl();

		if (baseTtl == null || baseTtl.isZero() || baseTtl.isNegative()) {
			return baseTtl;
		}

		long jitterMillis = jitter.toMillis();
		if (jitterMillis > 0) {
			long randomMillis = ThreadLocalRandom.current().nextLong(jitterMillis + 1);
			return baseTtl.plusMillis(randomMillis);
		}
		return baseTtl;
	}
}
