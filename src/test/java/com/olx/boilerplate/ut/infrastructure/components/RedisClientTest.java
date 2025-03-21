package com.olx.boilerplate.ut.infrastructure.components;

import com.google.gson.Gson;
import com.olx.boilerplate.infrastructure.components.RedisClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisClientTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOps;
    @Mock
    private ZSetOperations<String, String> zSetOps;
    @Mock
    private SetOperations<String, String> setOps;
    @Mock
    private Gson gson;

    @InjectMocks
    private RedisClient redisClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        redisClient = new RedisClient(redisTemplate, gson);
    }

    @Test
    void testPutValue() {
        redisClient.putValue("key", "value");
        verify(valueOps).set("key", "value");
    }

    @Test
    void testPutValueWithExpireTime() {
        redisClient.putValueWithExpireTime("key", "value", 10, TimeUnit.SECONDS);
        verify(valueOps).setIfAbsent("key", "value", 10, TimeUnit.SECONDS);
    }

    @Test
    void testGetValue() {
        when(valueOps.get("key")).thenReturn("value");
        assertEquals("value", redisClient.getValue("key"));
    }

    @Test
    void testExistsKey() {
        when(redisTemplate.hasKey("key")).thenReturn(true);
        assertTrue(redisClient.existsKey("key"));
    }

    @Test
    void testDeleteKey() {
        when(redisTemplate.delete("key")).thenReturn(true);
        assertTrue(redisClient.deleteKey("key"));
    }

    @Test
    void testPutSetValue() {
        redisClient.putSetValue("setKey", "value", 1.0);
        verify(zSetOps).add("setKey", "value", 1.0);
    }

    @Test
    void testRemoveSetValue() {
        redisClient.removeSetValue("setKey", "value");
        verify(zSetOps).remove("setKey", "value");
    }

    @Test
    void testAddToSet() {
        redisClient.addToSet("setKey", "value");
        verify(setOps).add("setKey", "value");
    }

    @Test
    void testRemoveFromSet() {
        redisClient.removeFromSet("setKey", "value");
        verify(setOps).remove("setKey", "value");
    }
}
