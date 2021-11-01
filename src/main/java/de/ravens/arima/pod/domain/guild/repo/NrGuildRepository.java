package de.ravens.arima.pod.domain.guild.repo;

import de.ravens.arima.pod.domain.guild.things.NrGuild;
import discord4j.common.util.Snowflake;

import java.util.Optional;

public interface NrGuildRepository {
    Optional<NrGuild> findByGuildId(Snowflake guildId);
    void save(NrGuild guild);
}
