package com.ai.pj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.ai.pj.repository.jpa")
@EnableRedisRepositories(basePackages = "com.ai.pj.repository.redis")
public class PersistenceConfig {
}
