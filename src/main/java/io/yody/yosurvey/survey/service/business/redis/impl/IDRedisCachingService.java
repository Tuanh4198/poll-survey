package io.yody.yosurvey.survey.service.business.redis.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.survey.service.business.redis.CachingService;
import org.redisson.api.*;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class IDRedisCachingService<V> implements CachingService<V> {

    final JsonJacksonCodec codec;
    private final RedissonClient redissonClient;

    @Autowired
    public IDRedisCachingService(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        this.redissonClient = ((RedissonConfiguration<Object, Object>) jcacheConfiguration).getRedisson();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        codec = new JsonJacksonCodec(objectMapper);
    }

    @Override
    public boolean hset(String key, String hashKey, V value, int expire) {
        RMapCache<String, Object> map = redissonClient.getMapCache(key, codec);
        map.put(hashKey, value, expire, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean hset(String key, String hashKey, V value) {
        RMapCache<String, Object> map = redissonClient.getMapCache(key, codec);
        map.put(hashKey, value);
        return true;
    }

    @Override
    public boolean set(String key, V value, int expire) {
        redissonClient.getBucket(key, codec).set(value, expire, TimeUnit.SECONDS);
        return false;
    }

    @Override
    public boolean set(String key, V value) {
        redissonClient.getBucket(key, codec).set(value);
        return false;
    }

    @Override
    public V get(String key) {
        RBucket<V> bucket = redissonClient.getBucket(key, codec);
        return Optional.ofNullable(bucket).map(RBucket::get).orElse(null);
    }

    @Override
    public Map<String, V> hget(String key) {
        RMap<String, V> map = redissonClient.getMapCache(key, codec);
        Map<String, V> result = new HashMap<>();
        for (Map.Entry<String, V> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public V hget(String key, String hash) {
        RMap<String, V> map = redissonClient.getMapCache(key, codec);
        return map.get(hash);
    }

    @Override
    public boolean exists(String key) {
        RObject object = redissonClient.getBucket(key);
        return object.isExists();
    }

    @Override
    public void evict(String key) {
        RObject object = redissonClient.getBucket(key);
        object.deleteAsync();
    }

    @Override
    public V getOrSet(String key, V value, int expire) {
        V exist = get(key);

        if (exist != null) return exist;

        set(key, value, expire);

        return value;
    }

    @Override
    public RAtomicLong getId(String key) {
        return redissonClient.getAtomicLong(key);
    }

    @Override
    public RLock getLock(String key) {
        return redissonClient.getLock(key);
    }
}
