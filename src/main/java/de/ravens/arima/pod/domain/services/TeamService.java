package de.ravens.arima.pod.domain.services;

import de.ravens.arima.pod.boundary.ddd.annotations.DomainService;
import de.ravens.arima.pod.domain.entity.Team;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

import java.util.List;

@DomainService
@Service
public class TeamService {

    //TODO Delete

    public Team addTeam(String name, String roleId, Guild guild){

        Team team = new Team(null, name, roleId, guild.getId().asString(), List.of());
        if(!roleMatchesGuild(guild, roleId)){
            throw new RuntimeException("Role not found in Guild");
        }
        return team;
    }

    public Team updateTeamName(Team team, String name){
        team.setName(name);
        return team;
    }

    public Team updateTeamRole(Team team, String roleId, Guild guild){
        if(!guildMatchesTeam(team, guild)){
            throw new RuntimeException("Given guild does not match team");
        }
        if(!roleMatchesGuild(guild, roleId)){
            throw new RuntimeException("Role not found in Guild");
        }
        team.setRoleId(roleId);
        return team;
    }

    private boolean roleMatchesGuild(Guild guild, String roleId){
        return guild.getRoleById(Snowflake.of(roleId)).blockOptional().isPresent();
    }

    private boolean guildMatchesTeam(Team team, Guild guild){
        return team.getGuildId().equals(guild.getId().asString());
    }
}
