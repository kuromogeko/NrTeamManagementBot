package de.ravens.arima.pod.commands;

import de.ravens.arima.pod.data.repo.CaptainConfigurationRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandFilters {
    public static final Predicate<Message> messageSentByBotPredicate = message -> message.getAuthor().map(user -> !user.isBot()).orElse(false);

    public static final Predicate<Message> messageSentByAdminPredicate = message -> message.getAuthorAsMember()
            .flatMap(member ->
                    Mono.zip(getMembersRolePermissions(member), member.getBasePermissions()))
            .map(objects -> objects.getT2().or(objects.getT1()).contains(Permission.ADMINISTRATOR))
            .onErrorReturn(false)
            .blockOptional().orElse(false);

    private static Mono<PermissionSet> getMembersRolePermissions(Member member) {
        return member.getRoles().map(Role::getPermissions)
                .collectList()
                .map(permissionSets -> permissionSets
                        .stream()
                        .reduce(PermissionSet::or).orElse(PermissionSet.none()));
    }



    @NotNull
    public static Predicate<Message> getHasCaptainRolePredicate(Snowflake guildID, CaptainConfigurationRepository configurationRepo) {
        return message -> message.getAuthorAsMember()
                .flatMap(member -> member.getRoles().collectList())
                .map(roles -> roles.stream()
                        .map(role -> role.getId().asString())
                        .collect(Collectors.toList()))
                .map(memberRoleIds -> {
                    var maybeCaptainConfig = configurationRepo.findByServerId(guildID.asString());
                    if (maybeCaptainConfig.isEmpty()) {
                        return false;
                    }
                    return memberRoleIds.contains(maybeCaptainConfig.get().getCaptainRoleId());
                })
                .blockOptional().orElse(false);
    }
}
