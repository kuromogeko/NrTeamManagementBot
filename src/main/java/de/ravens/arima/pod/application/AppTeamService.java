package de.ravens.arima.pod.application;

import de.ravens.arima.pod.application.dto.TeamDto;
import de.ravens.arima.pod.boundary.ddd.annotations.ApplicationService;
import de.ravens.arima.pod.boundary.rest.security.CustomDiscordUserPrincipal;
import de.ravens.arima.pod.domain.repo.GuildSettingRepository;
import de.ravens.arima.pod.domain.repo.TeamRepository;
import de.ravens.arima.pod.domain.services.GuildSecuredService;
import de.ravens.arima.pod.domain.services.TeamService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@ApplicationService
@AllArgsConstructor
public class AppTeamService {
    private final TeamService teamService;
    //    private final TeamMemberService teamMemberService;
    private final GuildSecuredService guildSecuredService;
    private final GuildSettingRepository guildSettingRepository;
    private final TeamRepository teamRepository;


    public TeamDto addTeam(String guildId, TeamDto teamDto, GatewayDiscordClient client, CustomDiscordUserPrincipal principal) {
        var user = client.getUserById(Snowflake.of(principal.getDiscordId())).blockOptional().orElseThrow();
        var guildSettings = guildSettingRepository.findByServerId(guildId).orElseGet(() -> {
            var defaultSettings = guildSecuredService.createDefaultGuildSettings(guildId);
            guildSettingRepository.save(defaultSettings);
            return defaultSettings;
        });
        var guild = client.getGuildById(Snowflake.of(guildId)).blockOptional().orElseThrow();
        if (!guildSecuredService.mayAccessFunction(guildSettings, guild, user)) {
            throw new RuntimeException("No access allowed");
        }
        var team = teamService.addTeam(teamDto.getName(), teamDto.getRoleId(), guild);
        teamRepository.save(team);
        return TeamDto.mapFromTeam(team);
    }
}
