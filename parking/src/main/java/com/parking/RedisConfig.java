package com.parking;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Redis configuration for distributed session persistence.
 * Enables Spring Session with Redis as the session store,
 * allowing the application to scale horizontally.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisConfig {
    // Configuration is provided via application.yaml
    // Sessions will be stored in Redis and shared across multiple instances
}
