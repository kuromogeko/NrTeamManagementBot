package de.ravens.arima.pod.boundary.repo.guild;

import de.ravens.arima.pod.domain.guild.repo.NrGuildRepository;
import de.ravens.arima.pod.domain.guild.things.GuildSettings;
import de.ravens.arima.pod.domain.guild.things.NrGuild;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@AllArgsConstructor
public class NrGuildRepositoryImpl implements NrGuildRepository {
    private final MongoGuildRepository repository;
    private final GatewayDiscordClient gatewayDiscordClient;

    @Override
    public Optional<NrGuild> findByGuildId(Snowflake guildId) {
        var rGuild = this.repository.findById(guildId.asString());
        var innerGuild = this.gatewayDiscordClient.getGuildById(guildId).block();
        if (innerGuild == null) {
            return Optional.empty();
        }
        GuildSettings settings = getSettings(rGuild);
        return Optional.of(new NrGuild(innerGuild, settings));
    }

    @Override
    public void save(NrGuild guild) {
        var rguild = new RGuild();
        rguild.setId(guild.getId().asString());
        rguild.setSettings(guild.getSettings());
        this.repository.save(rguild);
    }

    private GuildSettings getSettings(Optional<RGuild> rGuild) {
        GuildSettings settings;
        if (rGuild.isEmpty()) {
            settings = new GuildSettings(new ArrayList<>());
        } else {
            settings = rGuild.get().getSettings();
        }
        return settings;
    }
}
