package de.ravens.arima.pod.boundary.rest.security;

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
    private final DiscordOauthClient client;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        CustomDiscordUserPrincipal principal = null;
        if (null == auth) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token could not be validated");
            return;
        }
        if (cache.containsKey(auth)) {
            principal = cache.get(auth);
        } else {
            principal = loadCustomDiscordUserPrincipal(response, auth);
            if (principal == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token could not be validated");
                return;
            }
            cache.put(auth, principal);
        }
        CustomAuthentication authentication = new CustomAuthentication(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private CustomDiscordUserPrincipal loadCustomDiscordUserPrincipal(HttpServletResponse response, String auth) throws IOException {
        CustomDiscordUserPrincipal principal = null;
        try {
            principal = client.getDiscordPrincipal(auth);
        } catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token could not be validated");
            return null;
        }
        return principal;
    }
}
