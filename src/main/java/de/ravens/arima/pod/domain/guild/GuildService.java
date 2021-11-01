package de.ravens.arima.pod.domain.guild;

import de.ravens.arima.pod.domain.guild.repo.NrGuildRepository;
import de.ravens.arima.pod.domain.guild.things.GuildException;
import de.ravens.arima.pod.domain.guild.things.NrGuild;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GuildService implements GuildUseCase {
    private final NrGuildRepository repository;

    @Override
    public void addModifier(Snowflake guildId, Snowflake userId, Snowflake callingUser) throws GuildException {
        var guild = loadGuild(guildId);
        throwOnNoAccess(callingUser, guild);
        guild.addAccess(userId);
        this.repository.save(guild);
    }

    @Override
    public void removeModifier(Snowflake guildId, Snowflake userId, Snowflake callingUser) throws GuildException {
        var guild = loadGuild(guildId);
        throwOnNoAccess(callingUser, guild);
        guild.removeAccess(userId);
        this.repository.save(guild);
    }

    @Override
    public boolean mayModifyGuild(Snowflake guildId,  Snowflake callingUser) throws GuildException {
        var guild = loadGuild(guildId);
        return guild.mayModifyGuild(callingUser);
    }

    @Override
    public List<Member> getGuildMembers(Snowflake guildId) throws GuildException {
        var guild = loadGuild(guildId);
        return guild.getGuildMembers();
    }

    private NrGuild loadGuild(Snowflake guildId) throws GuildException {
        var oGuild = this.repository.findByGuildId(guildId);
        if (oGuild.isEmpty()) {
            throw new GuildException(String.format("No Guild found using guildId %s", guildId.asString()));
        }
        return oGuild.get();
    }

    private void throwOnNoAccess(Snowflake callingUser, NrGuild guild) throws GuildException {
        if (!guild.mayModifyGuild(callingUser)) {
            throw new GuildException("User not permitted to modify guild");
        }
    }
}
