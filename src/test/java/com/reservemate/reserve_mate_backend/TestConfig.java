package com.reservemate.reserve_mate_backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@TestConfiguration
public class TestConfig {

    /**
     * Redis connection factory for tests
     * This avoids the need for a real Redis server in tests
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        // For tests, we use an in-memory Redis
        return new LettuceConnectionFactory();
    }

    // Add other test-specific beans if needed
}