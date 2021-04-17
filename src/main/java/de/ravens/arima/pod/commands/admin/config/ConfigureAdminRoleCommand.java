package de.ravens.arima.pod.commands.admin.config;

import de.ravens.arima.pod.event.EventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ConfigureAdminRoleCommand implements EventListener<MessageCreateEvent> {
    Logger LOG = LoggerFactory.getLogger(ConfigureAdminRoleCommand.class);


    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        LOG.info(event.getMessage().getContent()
        );
        return Mono.just(event.getMessage())
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getAuthorAsMember()
                        .flatMap(member ->
                                Mono.zip(member.getRoles().map(Role::getPermissions)
                                        .collectList()
                                        .map(permissionSets -> permissionSets
                                                .stream()
                                                .reduce(PermissionSet::or)), member.getBasePermissions())
                        )
                        .map(objects -> objects.getT2().or(objects.getT1().orElse(PermissionSet.none())).contains(Permission.ADMINISTRATOR))
                        .onErrorReturn(false)
                        .blockOptional().orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!test"))
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("Test")).then();
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return null;
    }
}
