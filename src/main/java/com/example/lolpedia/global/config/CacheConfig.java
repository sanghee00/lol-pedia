package com.example.lolpedia.global.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
@Profile("!test")
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager redisCacheManager = buildRedisCacheManager(connectionFactory);
        redisCacheManager.afterPropertiesSet();

        List<Cache> caches = new ArrayList<>();

        // L1(Caffeine) + L2(Redis) 캐시
        // key 종류가 적고 변경이 드문 캐시 → L1 TTL 짧게 설정
        caches.add(new TwoLevelCache("players-top8", Duration.ofMinutes(5), 10,
            redisCacheManager.getCache("players-top8")));
        caches.add(new TwoLevelCache("teams-top8", Duration.ofMinutes(5), 10,
            redisCacheManager.getCache("teams-top8")));
        caches.add(new TwoLevelCache("player-info", Duration.ofMinutes(3), 200,
            redisCacheManager.getCache("player-info")));
        caches.add(new TwoLevelCache("team-detail", Duration.ofMinutes(3), 200,
            redisCacheManager.getCache("team-detail")));

        // L2(Redis) 전용 캐시 - key 공간이 넓어 L1 효과 미미
        caches.add(redisCacheManager.getCache("match-series-period"));
        caches.add(redisCacheManager.getCache("match-info"));

        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caches);
        return simpleCacheManager;
    }

    private RedisCacheManager buildRedisCacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = buildCacheObjectMapper();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
            .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
            "match-series-period", defaultConfig.entryTtl(Duration.ofHours(2)),
            "match-info", defaultConfig.entryTtl(Duration.ofHours(1)),
            "players-top8", defaultConfig.entryTtl(Duration.ofHours(24)),
            "teams-top8", defaultConfig.entryTtl(Duration.ofHours(24)),
            "player-info", defaultConfig.entryTtl(Duration.ofHours(4)),
            "team-detail", defaultConfig.entryTtl(Duration.ofHours(4))
        );

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigs)
            .build();
    }

    private ObjectMapper buildCacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.example.lolpedia.")
            .allowIfSubType("java.util.")
            .allowIfSubType("java.time.")
            .allowIfSubType("org.springframework.data.domain.")
            .build();

        mapper.activateDefaultTyping(
            typeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        SimpleModule pageModule = new SimpleModule();
        pageModule.addDeserializer(PageImpl.class, new PageImplDeserializer());
        mapper.registerModule(pageModule);

        return mapper;
    }

    @SuppressWarnings("rawtypes")
    private static class PageImplDeserializer extends StdDeserializer<PageImpl> {

        public PageImplDeserializer() {
            super(PageImpl.class);
        }

        @Override
        public PageImpl deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            JsonNode node = p.getCodec().readTree(p);

            int page = node.get("number").asInt();
            int size = node.get("size").asInt();
            long totalElements = node.get("totalElements").asLong();

            JsonNode contentNode = node.get("content");
            List<Object> content = new ArrayList<>();
            if (contentNode != null) {
                // Jackson serializes List with WRAPPER_ARRAY: ["java.util.ArrayList", [...items...]]
                JsonNode items = contentNode;
                if (contentNode.isArray() && contentNode.size() == 2 && contentNode.get(0).isTextual()) {
                    items = contentNode.get(1);
                }
                if (items != null && items.isArray()) {
                    for (JsonNode item : items) {
                        content.add(p.getCodec().treeToValue(item, Object.class));
                    }
                }
            }

            return new PageImpl<>(content, PageRequest.of(page, size), totalElements);
        }
    }
}
