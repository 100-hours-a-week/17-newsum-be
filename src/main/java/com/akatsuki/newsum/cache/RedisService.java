package com.akatsuki.newsum.cache;

import java.time.Duration;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	public void save(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public void addSetValue(String key, Object value) {
		redisTemplate.opsForSet().add(key, value);
	}

	public void removeSetValue(String key, Object value) {
		redisTemplate.opsForSet().remove(key, value);
	}

	public Set<Object> getSetMembers(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	//keys * 로 가져오기 때문에 최적화 필요함
	public Set<String> getKeys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	public void setExpire(String key, Duration ttl) {
		redisTemplate.expire(key, ttl);
	}

	public Long getSetSize(String key) {
		return redisTemplate.opsForSet().size(key);
	}
}
