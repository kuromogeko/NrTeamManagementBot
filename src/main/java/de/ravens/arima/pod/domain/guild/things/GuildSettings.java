package de.ravens.arima.pod.domain.guild.things;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class GuildSettings {
    private final List<Snowflake> modifierIds;

    public List<Snowflake> getModifierIds() {
        return List.copyOf(this.modifierIds);
    }

    public void addModifier(Snowflake permittedUser) {
        this.modifierIds.add(permittedUser);
    }

    public void removeModifier(Snowflake permittedUser) {

    }
}
