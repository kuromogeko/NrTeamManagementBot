package de.ravens.arima.pod.boundary.rest.security;

import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Cache<String, CustomDiscordUserPrincipal> cache;
    private final DiscordOauthClient client;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .addFilterAfter(new CustomAuthenticationFilter(cache, client), ConcurrentSessionFilter.class)
            .requestMatchers()
            .antMatchers("/api/**")
            .and()
                .authorizeRequests()
                .antMatchers("/api/**")
                .authenticated()
            .and()
                .cors()
        .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

}
