package de.ravens.arima.pod.commands;

import de.ravens.arima.pod.event.EventListener;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class PingCommand implements EventListener<MessageCreateEvent> {

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(message -> message.getContent().startsWith("_ping"))
                .flatMap(message -> message.getChannel())
                .flatMap(messageChannel -> messageChannel.createMessage("Pong"))
                .then();
    }
}
