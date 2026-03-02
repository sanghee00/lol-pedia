package com.example.lolpedia.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * L1(Caffeine) + L2(Redis) 2단 캐시.
 * 조회: L1 → L2 → DB 순으로 확인하며, L2 히트 시 L1을 채워 다음 요청을 가속.
 * 쓰기: L1/L2 동시에 기록.
 * 무효화: L1/L2 동시에 삭제.
 */
public class TwoLevelCache extends AbstractValueAdaptingCache {

    private final String name;
    private final Cache l1; // Caffeine (in-memory, ~0.01ms)
    private final Cache l2; // Redis (~2ms)

    public TwoLevelCache(String name, Duration l1Ttl, int l1MaxSize, Cache redisCache) {
        super(false);
        this.name = name;
        var nativeCache = Caffeine.newBuilder()
            .expireAfterWrite(l1Ttl)
            .maximumSize(l1MaxSize)
            .build();
        this.l1 = new CaffeineCache(name, nativeCache, false);
        this.l2 = redisCache;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return l1.getNativeCache();
    }

    @Override
    protected Object lookup(Object key) {
        // L1 히트
        ValueWrapper l1Wrapper = l1.get(key);
        if (l1Wrapper != null) {
            return toStoreValue(l1Wrapper.get());
        }

        // L2 히트 → L1 warm-up
        ValueWrapper l2Wrapper = l2.get(key);
        if (l2Wrapper != null) {
            l1.put(key, l2Wrapper.get());
            return toStoreValue(l2Wrapper.get());
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper cached = get(key);
        if (cached != null) {
            return (T) cached.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        l1.put(key, value);
        l2.put(key, value);
    }

    @Override
    public void evict(Object key) {
        l1.evict(key);
        l2.evict(key);
    }

    @Override
    public void clear() {
        l1.clear();
        l2.clear();
    }
}
