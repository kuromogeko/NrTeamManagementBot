package de.ravens.arima.pod.boundary.rest.controller;

import de.ravens.arima.pod.boundary.rest.security.CustomDiscordUserPrincipal;
import de.ravens.arima.pod.domain.guild.GuildUseCase;
import de.ravens.arima.pod.domain.guild.things.GuildException;
import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class GuildController {
    private final GuildUseCase guildUseCase;

    @PostMapping(path = "/api/guilds/{guildId}/editors")
    public ResponseEntity<?> addEditor(@RequestBody SnowflakeDto snowflakeDto,
                                       @AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                       @PathVariable String guildId) {
        try {
            this.guildUseCase.addModifier(Snowflake.of(guildId), Snowflake.of(snowflakeDto.getId()), Snowflake.of(principal.getDiscordId()));
            return ResponseEntity.ok().build();
        } catch (GuildException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @DeleteMapping(path = "/api/guilds/{guildId}/editors")
    public ResponseEntity<?> removeEditor(@RequestBody SnowflakeDto snowflakeDto,
                                          @AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                          @PathVariable String guildId) {
        try {
            this.guildUseCase.removeModifier(Snowflake.of(guildId), Snowflake.of(snowflakeDto.getId()), Snowflake.of(principal.getDiscordId()));
            return ResponseEntity.ok().build();
        } catch (GuildException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping(path = "/api/guilds/{guildId}")
    public ResponseEntity<?> getMembers(@AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                        @PathVariable String guildId) {
        try {
            return ResponseEntity.ok(this.guildUseCase.getGuildMembers(Snowflake.of(guildId)));
        } catch (GuildException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

}
