package de.ravens.arima.pod.commands.admin.command;

import de.ravens.arima.pod.commands.CommandConfiguration;
import de.ravens.arima.pod.commands.util.DisplayUtil;
import de.ravens.arima.pod.data.entitiy.ConfigurationEntity;
import de.ravens.arima.pod.data.entitiy.TeamCaptainGuildEntity;
import de.ravens.arima.pod.data.repo.CaptainConfigurationRepository;
import de.ravens.arima.pod.data.repo.TeamCaptainGuildRepository;
import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class SetCaptainWithRoleCommand implements EventListener<MessageCreateEvent> {
    private final CommandConfiguration configuration;
    private final TeamCaptainGuildRepository teamCaptainGuildRepository;
    private final CaptainConfigurationRepository captainConfigurationRepository;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    //TODO Continue here
    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(message -> message.getContent().startsWith("!addCaptain"))
                .map(this::buildFlowObject)
                .filter(SetCaptainWithRoleCommand::getAdminRolePredicate)
                .flatMap(this::getCaptainAndRoleMentions)
                .flatMap(this::setCaptainRoleToUsers)
                .map(this::persistCaptainRoleRelations) //  Tuple3<List<TeamCaptainGuildEntity>, MessageChannel, String>>
                .flatMap(flow ->
                        flow.getChannel().createMessage(messageCreateSpec ->
                                messageCreateSpec.setContent(flow.getResponse() + "\n" +
                                        DisplayUtil.displayCaptainShipOfTeams(flow.getDoneCaptains()))))
                .then();
    }

    private Mono<FlowObject> setCaptainRoleToUsers(FlowObject flow) {
        var captainConfig = captainConfigurationRepository.findByServerId(flow.getGuildId().asString());
        if (captainConfig.isEmpty()) {
            flow.setResponse(flow.getResponse() + "No Captain role configured, skipped setting the role");
            return Mono.just(flow);
        }
        return Flux.fromIterable(flow.getToBeMadeCaptains())
                .flatMap(snowflake -> flow.getClient().getMemberById(flow.getGuildId(), snowflake)
                        .flatMap(member -> member.addRole(Snowflake.of(captainConfig.get().getCaptainRoleId()))))
                .collectList()
                .thenReturn(String.format("Added captain role  <@&%s> for Users", captainConfig.get().getCaptainRoleId()))
                .onErrorReturn("Error settings captain roles, skipped")
                .map(s -> {
                    flow.setResponse(flow.getResponse() + s);
                    return flow;
                });
    }

    @NotNull
    // Function<Tuple4<Set<Snowflake>, List<Role>, MessageChannel, String>,
    private FlowObject persistCaptainRoleRelations(FlowObject flow) {
        var captainList = flow.getToBeMadeCaptains()
                .stream()
                .map(Snowflake::asString)
                .collect(Collectors.toList());
        var roleList = flow.getTeamMentions()
                .stream()
                .map(role -> role.getId().asString())
                .collect(Collectors.toList());
        // saving is done here
        var teamCaptainCombinations = addNewCaptainTeamCombinations(flow.getGuildId().asString(), captainList, roleList);
        flow.setDoneCaptains(teamCaptainCombinations);
        return flow;
    }

    @NotNull
    private Mono<FlowObject> getCaptainAndRoleMentions(FlowObject flow) {
        var message = flow.getMessage();
        var teamMentions = message.getRoleMentions().collectList();
        var captainMentions = message.getUserMentionIds();
        return Mono.zip(teamMentions, message.getChannel()).map(objects -> {
            flow.setChannel(objects.getT2());
            flow.setTeamMentions(objects.getT1());
            flow.setToBeMadeCaptains(captainMentions);
            return flow;
        });
    }

    private List<TeamCaptainGuildEntity> addNewCaptainTeamCombinations(String serverId, List<String> userIds, List<String> roleIds) {
        var result = new ArrayList<TeamCaptainGuildEntity>();
        for (String userId : userIds) {
            for (String roleId : roleIds) {
                var entry = teamCaptainGuildRepository
                        .findByServerIdAndUserIdAndRoleId(serverId, userId, roleId)
                        .orElse(new TeamCaptainGuildEntity(null, serverId, userId, roleId));
                result.add(teamCaptainGuildRepository.save(entry));
            }
        }
        return result;
    }


    public static boolean getAdminRolePredicate(FlowObject object) {
        var message = object.getMessage();
        return message.getAuthorAsMember()
                .flatMap(member -> member.getRoles().collectList())
                .map(roles -> roles.stream()
                        .map(role -> role.getId().asString())
                        .collect(Collectors.toList()))
                .map(memberRoleIds -> {
                    var listOfPermittedRoleIds = object.getConfiguration()
                            .findOrCreateCommandConfig(object.getGuildId().asString())
                            .getRoles()
                            .stream()
                            .map(ConfigurationEntity::getRoleId)
                            .collect(Collectors.toList());
                    return !Collections.disjoint(listOfPermittedRoleIds, memberRoleIds);
                })
                .blockOptional().orElse(false);
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error(error.getMessage());
        return Mono.empty();
    }

    private FlowObject buildFlowObject(Message message) {
        return FlowObject.builder()
                .message(message)
                .guildId(message.getGuildId().orElseThrow())
                .configuration(this.configuration)
                .build();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    private static class FlowObject {
        private Snowflake guildId;
        private GatewayDiscordClient client;
        private Message message;
        private CommandConfiguration configuration;
        private List<Role> teamMentions;
        private Set<Snowflake> toBeMadeCaptains;
        private List<TeamCaptainGuildEntity> doneCaptains;
        private MessageChannel channel;
        private String response;
    }
}
