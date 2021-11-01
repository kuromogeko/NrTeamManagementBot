package de.ravens.arima.pod.domain.team;

import de.ravens.arima.pod.domain.guild.things.GuildException;
import de.ravens.arima.pod.domain.team.things.Team;
import de.ravens.arima.pod.domain.team.things.TeamException;
import discord4j.common.util.Snowflake;

import java.util.List;

public interface TeamUseCase {
    void addTeam(Team team, Snowflake callingUser) throws GuildException, TeamException;
    void changeTeam(Team team, Snowflake callingUser) throws TeamException, GuildException;
    void removeTeam(String id, Snowflake callingUser) throws TeamException, GuildException;

    List<Team> getAllTeams(Snowflake guildId);
    Team getTeam(String id) throws TeamException;
}
