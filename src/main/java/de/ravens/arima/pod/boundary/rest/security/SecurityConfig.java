package de.ravens.arima.pod.boundary.rest.security;

import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

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
            .antMatchers("/**")
            .and()
                .authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .cors()
        .and().csrf().disable();
    }

}
