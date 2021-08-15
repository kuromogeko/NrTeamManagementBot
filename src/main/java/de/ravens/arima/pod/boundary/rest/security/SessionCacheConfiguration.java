package de.ravens.arima.pod.boundary.rest.security;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
public class SessionCacheConfiguration {

        @Bean
        public Cache<String, CustomDiscordUserPrincipal> sessionCache() {
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
            CacheConfigurationBuilder<String, CustomDiscordUserPrincipal> sessionCacheConfigurationBuilder = CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(String.class, CustomDiscordUserPrincipal.class, ResourcePoolsBuilder.heap(200).build())
                    .withExpiry(new SessionCacheExpiration());
            return cacheManager
                    .createCache("sessionCache", sessionCacheConfigurationBuilder);
        }

}
