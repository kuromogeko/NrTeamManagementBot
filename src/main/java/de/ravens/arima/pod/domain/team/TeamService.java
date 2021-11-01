package de.ravens.arima.pod.domain.team;

import de.ravens.arima.pod.domain.guild.GuildUseCase;
import de.ravens.arima.pod.domain.guild.things.GuildException;
import de.ravens.arima.pod.domain.team.repo.TeamRepository;
import de.ravens.arima.pod.domain.team.things.Team;
import de.ravens.arima.pod.domain.team.things.TeamException;
import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamService implements TeamUseCase {
    private final TeamRepository repository;
    private final GuildUseCase guildUseCase;

    @Override
    public void addTeam(Team team, Snowflake callingUser) throws GuildException, TeamException {
        throwOnNoAccess(team, callingUser);
        var oTeam = this.repository.findById(team.getId());
        if (oTeam.isPresent()) {
            throw new TeamException("Team already exists or id given which is not allowed when adding a team");
        }
        this.repository.save(team);
    }


    @Override
    public void changeTeam(Team team, Snowflake callingUser) throws TeamException, GuildException {
        throwOnNoAccess(team, callingUser);
        var oTeam = this.repository.findById(team.getId());
        if (oTeam.isEmpty()) {
            throw new TeamException("No Team to modify could be found, consider adding one first");
        }
        this.repository.save(team);
    }

    @Override
    public void removeTeam(String id, Snowflake callingUser) throws TeamException, GuildException {
        var oTeam = this.repository.findById(id);
        if (oTeam.isEmpty()) {
            throw new TeamException("No Team to could be found");
        }
        throwOnNoAccess(oTeam.get(), callingUser);
        this.repository.delete(oTeam.get());
    }

    @Override
    public List<Team> getAllTeams(Snowflake guildId) {
        return this.repository.findAllByGuildId(guildId);
    }

    @Override
    public Team getTeam(String id) throws TeamException {
        return this.repository.findById(id).orElseThrow(() -> new TeamException("No Team found with given id"));
    }

    private void throwOnNoAccess(Team team, Snowflake callingUser) throws GuildException, TeamException {
        // As we have guildId in callers path we should always be able to circumvent this
        if (null == team.getGuildId()) {
            throw new TeamException("No Guild given");
        }
        if (!this.guildUseCase.mayModifyGuild(team.getGuildId(), callingUser)) {
            throw new TeamException("Not Allowed to accessGuild");
        }
    }
}
