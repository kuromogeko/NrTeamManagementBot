package de.ravens.arima.pod.domain.guild.things;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class NrGuild {
    private final Guild innerGuild;
    @Getter
    private final GuildSettings settings;

    public Snowflake getId() {
        return this.innerGuild.getId();
    }

    public boolean mayModifyGuild(Snowflake userId) {
        //  TODO Cleanup
        var isAdmin = this.innerGuild.getMemberById(userId).block().getBasePermissions().block().contains(Permission.ADMINISTRATOR);
        return isAdmin || this.settings.getModifierIds().contains(userId);
    }

    public void addAccess(Snowflake userId) {
        this.settings.addModifier(userId);
    }

    public void removeAccess(Snowflake userId) {
        this.settings.removeModifier(userId);
    }

    public List<Member> getGuildMembers(){
        return this.innerGuild.getMembers().collectList().block();
    }
}
