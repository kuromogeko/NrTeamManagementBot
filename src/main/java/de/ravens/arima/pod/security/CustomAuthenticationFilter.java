package de.ravens.arima.pod.security;

import lombok.RequiredArgsConstructor;
import org.ehcache.Cache;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends GenericFilterBean {
    private final Cache<String, CustomDiscordUserPrincipal> cache;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String user = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cache.containsKey(user)) {
            var principal = cache.get(user);
            CustomAuthentication authentication = new CustomAuthentication(principal);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        //TODO Discord Oauth2/@me call

        //TODO On not authenticated be very unhappy
        chain.doFilter(request, response);

    }
}
