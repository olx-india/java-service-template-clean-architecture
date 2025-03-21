package com.olx.boilerplate.infrastructure.components;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {

    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> valueOps;
    private final ZSetOperations<String, String> zSetOps;
    private final SetOperations<String, String> setOps;
    private final Gson gson;

    @Autowired
    public RedisClient(StringRedisTemplate redisTemplate, Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson = gson;
        this.valueOps = redisTemplate.opsForValue();
        this.setOps = redisTemplate.opsForSet();
        this.zSetOps = redisTemplate.opsForZSet();
    }

    public void putValue(String key, String value) {
        valueOps.set(key, value);
    }

    public void putValueWithExpireTime(String key, String value, long timeout, TimeUnit unit) {
        valueOps.setIfAbsent(key, value, timeout, unit);
    }

    public <T> void putValue(String key, T object) {
        valueOps.set(key, gson.toJson(object));
    }

    public <T> void putValueWithExpireTime(String key, T object, long timeout, TimeUnit unit) {
        valueOps.set(key, gson.toJson(object), timeout, unit);
    }

    public String getValue(String key) {
        return valueOps.get(key);
    }

    public <T> T getValue(String key, Class<T> type) {
        String value = valueOps.get(key);
        return StringUtils.isNotEmpty(value) ? gson.fromJson(value, (Type) type) : null;
    }

    public Boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    public void putSetValue(String key, String value, double score) {
        zSetOps.add(key, value, score);
    }

    public void removeSetValue(String key, String value) {
        zSetOps.remove(key, value);
    }

    public Set<String> getSetValuesByScore(String key, double minScore, double maxScore) {
        return zSetOps.rangeByScore(key, minScore, maxScore);
    }

    public void addToSet(String key, String value) {
        setOps.add(key, value);
    }

    public void removeFromSet(String key, String value) {
        setOps.remove(key, value);
    }

    public Set<String> readSetValues(String key) {
        return setOps.members(key);
    }

    public boolean deleteKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
