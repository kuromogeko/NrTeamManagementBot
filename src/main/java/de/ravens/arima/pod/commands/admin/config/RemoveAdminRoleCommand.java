package de.ravens.arima.pod.commands.admin.config;

import de.ravens.arima.pod.commands.CommandConfiguration;
import de.ravens.arima.pod.commands.util.DisplayUtil;
import de.ravens.arima.pod.data.entitiy.ConfigurationEntity;
import de.ravens.arima.pod.data.entitiy.ServerConfiguration;
import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.ravens.arima.pod.commands.CommandFilters.messageSentByAdminPredicate;
import static de.ravens.arima.pod.commands.CommandFilters.messageSentByBotPredicate;

@Component
@RequiredArgsConstructor
public class RemoveAdminRoleCommand implements EventListener<MessageCreateEvent> {

    private final CommandConfiguration configuration;


    Logger LOG = LoggerFactory.getLogger(RemoveAdminRoleCommand.class);


    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    //TODO TEST
    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(messageSentByBotPredicate)
                .filter(messageSentByAdminPredicate)
                .filter(message -> message.getContent().startsWith("!removeAdminRole"))
                .flatMap(this::removeMentionedRolesFromConfig)
                .flatMap(transport ->
                        transport.getMessageChannel()
                                .createEmbed(removeRolePermittedEmbedAndSendToChannel(transport)))
                .then();
    }

    @NotNull
    private Mono<FlowObject> removeMentionedRolesFromConfig(Message message) {
        var mentionedRoles = message.getRoleMentions().collectList()
                .flatMap(roles -> this.removeRolesFromConfig(roles, message.getGuildId().orElseThrow()));
        var channel = message.getChannel();
        return Mono.zip(mentionedRoles, channel)
                .map(objects -> FlowObject
                        .builder()
                        .rolesToBeRemoved(objects.getT1().getT1())
                        .configuration(objects.getT1().getT2())
                        .messageChannel(objects.getT2()).build());
    }

    @NotNull
    private Consumer<EmbedCreateSpec> removeRolePermittedEmbedAndSendToChannel(FlowObject transport) {
        return embedCreateSpec ->
                embedCreateSpec.setColor(Color.BISMARK)
                        .setAuthor("Pod", null, null)
                        .setTitle("Removed permitted roles")
                        .addField("Roles removed", DisplayUtil.displayListOfRoles(transport.getRolesToBeRemoved()), false)
                        .addField("Roles Permitted", DisplayUtil.displayListOfConfigEntities(transport.getConfiguration().getRoles()), false)
                        .setFooter("Pod Services", null)
                        .setTimestamp(Instant.now());
    }

    private Mono<Tuple2<List<Role>, ServerConfiguration>> removeRolesFromConfig(List<Role> roles, Snowflake snowflake) {

        return Mono.just(roles).map(roles1 -> {
            var config = configuration.findOrCreateCommandConfig(snowflake.asString());
            var mentioned = roles.stream()
                    .map(role -> new ConfigurationEntity(role.getId().asString(), role.getName())).collect(Collectors.toList());

            config.getRoles().removeAll(mentioned);
            var finalConf = configuration.saveServerConfiguration(config);
            return Tuples.of(roles1, finalConf);
        });
    }


    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error(error.getMessage());
        return Mono.empty();
    }

    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @Data
    private static class FlowObject {
        private List<Role> rolesToBeRemoved;
        private ServerConfiguration configuration;
        private MessageChannel messageChannel;
        private Snowflake serverId;
    }
}
