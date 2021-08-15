package de.ravens.arima.pod.boundary.rest.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
public class DiscordOauthClient {
    private final ObjectMapper mapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String oauth2Url = "https://discord.com/api/oauth2/@me";

    public CustomDiscordUserPrincipal getDiscordPrincipal(String auth) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        String token = auth.replace("Bearer ", "");
        headers.setBearerAuth(token);
        var response = restTemplate.exchange(oauth2Url, HttpMethod.GET, entity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Discord did not respond with OK");
        }
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode name = root.path("user").path("username");
        JsonNode expires = root.path("expires");
        var scopes = StreamSupport
                .stream(root.withArray("scopes").spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toList());

        return CustomDiscordUserPrincipal.builder()
                .name(name.asText())
                .expires(expires.asText())
                .scopes(scopes)
                .token(token)
                .build();
    }
}
