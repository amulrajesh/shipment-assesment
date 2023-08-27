package com.assignment.aggregationflux.config;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.assignment.aggregationflux.properties.ConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class ReactiveRedisConfiguration  {

    private final ConfigProperties properties;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(
            Objects.requireNonNull(properties.getCache().getHost()),
            Integer.parseInt(Objects.requireNonNull(properties.getCache().getPort()))
        );
    }

    @Bean
    public ReactiveRedisOperations<String, String> reactiveRedisOperations(ReactiveRedisConnectionFactory factory, ObjectMapper mapper) {
        Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(mapper, String.class);

        RedisSerializationContext<String, String> context =
            RedisSerializationContext.<String, String>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate(factory, context);
    }

}
