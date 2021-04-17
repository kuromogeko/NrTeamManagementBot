package de.ravens.arima.pod.commands.admin.config;

import de.ravens.arima.pod.data.entitiy.CaptainConfiguration;
import de.ravens.arima.pod.data.repo.CaptainConfigurationRepository;
import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.ravens.arima.pod.commands.CommandFilters.messageSentByAdminPredicate;
import static de.ravens.arima.pod.commands.CommandFilters.messageSentByBotPredicate;

@RequiredArgsConstructor
@Component
public class DefineCaptainRoleCommand implements EventListener<MessageCreateEvent> {

    private final CaptainConfigurationRepository repository;

    Logger LOG = LoggerFactory.getLogger(ConfigureAdminRoleCommand.class);

    private static Mono<FlowObject> getRoleListAndServerAndChannelFromContext(Message message) {
        return Mono.zip(message.getRoleMentions().collectList(), message.getChannel())
                .map(objects -> FlowObject.builder()
                        .channel(objects.getT2())
                        .mentionedRoles(objects.getT1())
                        .serverId(message.getGuildId().orElseThrow())
                        .build());
    }

    private static FlowObject selectFirstRole(FlowObject rolesChannelTuple) {
        rolesChannelTuple.setSelectedRole(rolesChannelTuple.getMentionedRoles().stream().findFirst().orElseThrow());
        return rolesChannelTuple;
    }

    private FlowObject createUpdateCaptainConfig(FlowObject role) {
        var savedConfig = repository
                .findByServerId(role.getServerId().asString())
                .orElse(new CaptainConfiguration(null, role.getServerId().asString(), null));
        savedConfig.setCaptainRoleId(role.getSelectedRole().getId().asString());
        role.setCaptainConfiguration(repository.save(savedConfig));
        return role;
    }

    private String createFormattedCaptainMessage(CaptainConfiguration captainConfiguration) {
        return String.format("Captain role for this Server is now <@&%s>", captainConfiguration.getCaptainRoleId());
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(messageSentByBotPredicate)
                .filter(messageSentByAdminPredicate)
                .filter(message -> message.getContent().startsWith("!setCaptainRole"))
                .flatMap(DefineCaptainRoleCommand::getRoleListAndServerAndChannelFromContext)
                .map(DefineCaptainRoleCommand::selectFirstRole)
                .map(this::createUpdateCaptainConfig)
                .flatMap(objects -> objects.getChannel()
                        .createMessage(createFormattedCaptainMessage(objects.getCaptainConfiguration())))
                .then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error(error.getMessage());
        return Mono.empty();
    }

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    private static class FlowObject {
        private List<Role> mentionedRoles;
        private MessageChannel channel;
        private Role selectedRole;
        private Snowflake serverId;
        private CaptainConfiguration captainConfiguration;
    }
}
