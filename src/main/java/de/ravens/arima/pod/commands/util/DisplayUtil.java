package de.ravens.arima.pod.commands.util;

import de.ravens.arima.pod.data.entitiy.ConfigurationEntity;
import de.ravens.arima.pod.data.entitiy.TeamCaptainGuildEntity;
import discord4j.core.object.entity.Role;

import java.util.List;

public class DisplayUtil {

    public static String displayListOfRoles(List<Role> roles) {
        return roles.stream().map(Role::getName).reduce((s, s2) -> s + " " + s2).orElse("None");
    }

    public static String displayListOfConfigEntities(List<ConfigurationEntity> roles) {
        return roles.stream().map(ConfigurationEntity::getName).reduce((s, s2) -> s + " " + s2).orElse("None");
    }

    public static String displayCaptainShipOfTeams(List<TeamCaptainGuildEntity> list) {
        return list.stream()
                .map(entity -> String.format("<@%s> is captain of <@&%s>\n", entity.getUserId(), entity.getRoleId()))
                .reduce((s, s2) -> s + s2)
                .orElse("Some error occured");
    }
}
