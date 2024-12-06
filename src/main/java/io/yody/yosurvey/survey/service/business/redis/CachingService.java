package io.yody.yosurvey.survey.service.business.redis;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;

import java.util.Map;

public interface CachingService<V> {
    boolean hset(String key, String hashKey, V value, int expire);

    boolean hset(String key, String hashKey, V value);

    boolean set(String key, V value, int expire);

    boolean set(String key, V value);

    V get(String key);

    Map<String, V> hget(String key);

    V hget(String key, String hash);

    boolean exists(String key);

    void evict(String key);

    V getOrSet(String key, V value, int expire);

    RAtomicLong getId(String key);

    RLock getLock(String key);
}
