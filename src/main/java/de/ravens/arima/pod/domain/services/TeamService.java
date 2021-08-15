package de.ravens.arima.pod.domain.services;

import de.ravens.arima.pod.domain.entity.Team;
import discord4j.core.object.entity.Guild;

public class TeamService {

    //TODO Add Update Delete


    private boolean guildMatchesTeam(Team team, Guild guild){
        return team.getGuildId().equals(guild.getId().asString());
    }
}
