package de.ravens.arima.pod.domain.guild;

import de.ravens.arima.pod.domain.guild.things.GuildException;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;

import java.util.List;

public interface GuildUseCase {
    void addModifier(Snowflake guildId, Snowflake userId,Snowflake callingUser) throws GuildException;
    void removeModifier(Snowflake guildId, Snowflake userId, Snowflake callingUser) throws GuildException;

    boolean mayModifyGuild(Snowflake guildId,  Snowflake callingUser) throws GuildException;

    List<Member> getGuildMembers(Snowflake guildId) throws GuildException;

}
