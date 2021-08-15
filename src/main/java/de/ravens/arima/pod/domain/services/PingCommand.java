package de.ravens.arima.pod.domain.services;

import de.ravens.arima.pod.application.event.EventListener;
import de.ravens.arima.pod.boundary.ddd.annotations.DomainService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
@DomainService
public class PingCommand implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(message -> message.getContent().startsWith("_ping"))
                .flatMap(Message::getChannel)
                .flatMap(messageChannel -> messageChannel.createMessage("Pong"))
                .then();
    }
}
