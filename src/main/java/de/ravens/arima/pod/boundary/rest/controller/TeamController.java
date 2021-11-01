package de.ravens.arima.pod.boundary.rest.controller;

import de.ravens.arima.pod.boundary.rest.security.CustomDiscordUserPrincipal;
import de.ravens.arima.pod.domain.guild.things.GuildException;
import de.ravens.arima.pod.domain.team.TeamUseCase;
import de.ravens.arima.pod.domain.team.things.Team;
import de.ravens.arima.pod.domain.team.things.TeamException;
import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TeamController {
    private final TeamUseCase teamUseCase;

    @PostMapping(path = "api/guilds/{guildId}/teams")
    public ResponseEntity<?> postTeam(@RequestBody TeamDto teamDto,
                                      @AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                      @PathVariable String guildId) {
        var team = new Team(teamDto.getId(), Snowflake.of(guildId), teamDto.getName(), teamDto.getMembersWhichAreNotCaptain(), teamDto.getCaptainId(), teamDto.getRoleId());
        try {
            this.teamUseCase.addTeam(team, Snowflake.of(principal.getDiscordId()));
        } catch (GuildException | TeamException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PatchMapping(path = "api/guilds/{guildId}/teams")
    public ResponseEntity<?> patchTeam(@RequestBody TeamDto teamDto,
                                       @AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                       @PathVariable String guildId) {
        var team = new Team(teamDto.getId(), Snowflake.of(guildId), teamDto.getName(), teamDto.getMembersWhichAreNotCaptain(), teamDto.getCaptainId(), teamDto.getRoleId());
        try {
            this.teamUseCase.changeTeam(team, Snowflake.of(principal.getDiscordId()));
        } catch (GuildException | TeamException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @DeleteMapping(path = "api/teams/{teamId}")
    public ResponseEntity<?> deleteTeam(@AuthenticationPrincipal CustomDiscordUserPrincipal principal,
                                        @PathVariable String teamId) {
        try {
            this.teamUseCase.removeTeam(teamId, Snowflake.of(principal.getDiscordId()));
        } catch (GuildException | TeamException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping(path = "api/guilds/{guildId}/teams")
    public ResponseEntity<?> listAllTeams(@PathVariable String guildId) {
        return ResponseEntity.ok(this.teamUseCase.getAllTeams(Snowflake.of(guildId)));
    }

    @GetMapping(path = "api/teams/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable String teamId) {
        try {
            return ResponseEntity.ok(this.teamUseCase.getTeam(teamId));
        } catch (TeamException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

}
