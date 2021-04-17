package de.ravens.arima.pod.commands.admin.config;

import de.ravens.arima.pod.commands.CommandConfiguration;
import de.ravens.arima.pod.commands.util.DisplayUtil;
import de.ravens.arima.pod.data.entitiy.ConfigurationEntity;
import de.ravens.arima.pod.data.entitiy.ServerConfiguration;
import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.ravens.arima.pod.commands.CommandFilters.messageSentByAdminPredicate;
import static de.ravens.arima.pod.commands.CommandFilters.messageSentByBotPredicate;

@Component
@RequiredArgsConstructor
public class ConfigureAdminRoleCommand implements EventListener<MessageCreateEvent> {

    private final CommandConfiguration configuration;


    Logger LOG = LoggerFactory.getLogger(ConfigureAdminRoleCommand.class);


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
                .filter(message -> message.getContent().startsWith("!setAdminRole"))
                .flatMap(message -> {
                    var mentionedRoles = message.getRoleMentions().collectList()
                            .flatMap(roles -> this.addRolesToConfig(roles, message.getGuildId()));
                    var channel = message.getChannel();
                    return Mono.zip(mentionedRoles, channel);
                })
                .flatMap(objects ->
                        objects.getT2().createEmbed(createRolePermittedEmbedAndSendToChannel(objects)))
                .then();
    }

    @NotNull
    private Consumer<EmbedCreateSpec> createRolePermittedEmbedAndSendToChannel(reactor.util.function.Tuple2<Tuple2<List<Role>, ServerConfiguration>, MessageChannel> objects) {
        return embedCreateSpec ->
                embedCreateSpec.setColor(Color.BISMARK)
                        .setAuthor("Pod", null, null)
                        .setTitle("Added permitted roles")
                        .addField("Roles added", DisplayUtil.displayListOfRoles(objects.getT1().getT1()), false)
                        .addField("Roles Permitted", DisplayUtil.displayListOfConfigEntities(objects.getT1().getT2().getRoles()), false)
                        .setFooter("Pod Services", null)
                        .setTimestamp(Instant.now());
    }

    private Mono<Tuple2<List<Role>, ServerConfiguration>> addRolesToConfig(List<Role> roles, Optional<Snowflake> snowflake) {
        if (snowflake.isEmpty()) {
            return Mono.error(new RuntimeException());
        }

        return Mono.just(roles).map(roles1 -> {
            var config = configuration.findOrCreateCommandConfig(snowflake.get().asString());
            var newRoles = roles.stream()
                    .map(role -> new ConfigurationEntity(role.getId().asString(), role.getName()))
                    .filter(configurationEntity -> !config.getRoles().contains(roles))
                    .collect(Collectors.toList());
            config.getRoles().addAll(newRoles);
            var finalConf = configuration.saveServerConfiguration(config);
            return Tuples.of(roles1, finalConf);
        });
    }


    @Override
    public Mono<Void> handleError(Throwable error) {
        LOG.error(error.getMessage());
        return Mono.empty();
    }
}
