// src/main/java/com/interview/service_p/config/RedisConfig.java
package com.interview.service_p.config;

import com.interview.service_p.model.TickerStatistic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, TickerStatistic> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TickerStatistic> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // --- Key Serializer Configuration ---
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // --- Value Serializer Configuration ---
        // Configure ObjectMapper first
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // FIXED: Pass ObjectMapper directly into the constructor of Jackson2JsonRedisSerializer
        Jackson2JsonRedisSerializer<TickerStatistic> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, TickerStatistic.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
