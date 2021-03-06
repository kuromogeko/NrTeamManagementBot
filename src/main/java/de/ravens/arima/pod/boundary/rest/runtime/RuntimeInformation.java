package de.ravens.arima.pod.boundary.rest.runtime;

import de.ravens.arima.pod.application.dto.InfoDto;
import de.ravens.arima.pod.boundary.rest.security.CustomDiscordUserPrincipal;
import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RuntimeInformation {
    private final GatewayDiscordClient client;

    @GetMapping("/api/info")
    public ResponseEntity<String> gatewayClientState() {
        return ResponseEntity.ok(client.getSelfId().asString());
    }

    @GetMapping("/api/cred")
    public ResponseEntity<InfoDto> getCredInfo(@AuthenticationPrincipal CustomDiscordUserPrincipal principal) {
        return ResponseEntity.ok(InfoDto.builder()
                .name(principal.getName())
                .scopes(principal.getScopes())
                .discordId(principal.getDiscordId()).build());
    }
}
