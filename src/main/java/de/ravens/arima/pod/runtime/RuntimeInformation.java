package de.ravens.arima.pod.runtime;

import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RuntimeInformation {
    private final GatewayDiscordClient client;

    @GetMapping("/api/info")
    public ResponseEntity<String> gatewayClientState() {
        return ResponseEntity.ok(client.getSelfId().asString());
    }
}
