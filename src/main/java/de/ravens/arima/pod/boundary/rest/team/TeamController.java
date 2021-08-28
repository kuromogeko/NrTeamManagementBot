package de.ravens.arima.pod.boundary.rest.team;

import de.ravens.arima.pod.application.AppTeamService;
import de.ravens.arima.pod.application.dto.TeamDto;
import de.ravens.arima.pod.boundary.rest.security.CustomDiscordUserPrincipal;
import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TeamController {
    private final GatewayDiscordClient client;
    private final AppTeamService teamService;

    @PostMapping(path = "api/guilds/{guildId}/teams")
    public ResponseEntity<TeamDto> postTeam(@RequestBody TeamDto teamDto,
                                            @AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                            @PathVariable String guildId){
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.addTeam(guildId, teamDto, client, principal));
    }
}
