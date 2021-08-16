package de.ravens.arima.pod.domain.services;

import de.ravens.arima.pod.boundary.ddd.annotations.DomainService;
import de.ravens.arima.pod.domain.entity.GuildSettings;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Permission;
import org.springframework.stereotype.Service;

import java.util.List;

@DomainService
@Service
public class GuildSecuredService {

    /**
     * @param settings The Settings for the guild
     * @param member The member which made the call
     * @return wether access is granted
     */
    private boolean mayAccessGuild(GuildSettings settings, Member member){
        if(!settingsMatchMember(settings, member)){
            throw new RuntimeException("Wrong member given for settings");
        }
        return settings.getPermittedIds().contains(member.getId().asString()) || memberIsAdmin(member);
    }


    /**
     * @param settings settings for the server
     * @param member member of the server
     * @return wether the member is in the guild that has been given
     */
    private boolean settingsMatchMember(GuildSettings settings, Member member) {
        return member.getGuildId().asString().equals(settings.getServerId());
    }

    /**
     * @param member User which called, element is specific to guild
     * @return wether the member has administrative Privilege on the server
     */
    private boolean memberIsAdmin(Member member) {
        return member
                .getBasePermissions()
                .map(permissions -> permissions.contains(Permission.ADMINISTRATOR))
                .blockOptional()
                .orElse(false);
    }

    private boolean guildMatchesGuildSetting(GuildSettings settings, Guild guild){
        return settings.getServerId().equals(guild.getId().asString());
    }

    /**
     * @param settings Settings for member
     * @param member member to permit
     * @return the new settings (which still have to be saved)
     */
    public GuildSettings allowAccessToGuild(GuildSettings settings, Member member){
        if(!settingsMatchMember(settings, member)){
            throw new RuntimeException("Settings can not add a member that does not match the selected guild");
        }
        settings.getPermittedIds().add(member.getId().asString());
        return settings;
    }

    public GuildSettings createDefaultGuildSettings(String serverId){
        return new GuildSettings(serverId, List.of());
    }

    private boolean userIsInGuild(String guildId, User user){
        return user.asMember(Snowflake.of(guildId)).blockOptional().isPresent();
    }

    public boolean mayAccessFunction(GuildSettings settings, Guild guild, User user){
        if(!guildMatchesGuildSetting(settings, guild)){
            return false;
        }
        var member = user.asMember(Snowflake.of(settings.getServerId())).blockOptional().orElseThrow();
        if(!settingsMatchMember(settings, member)){
            return false;
        }
        return mayAccessGuild(settings, member);
    }

}
