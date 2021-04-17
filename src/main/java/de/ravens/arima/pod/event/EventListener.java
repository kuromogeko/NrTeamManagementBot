package de.ravens.arima.pod.event;

import discord4j.core.event.domain.Event;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;


public interface EventListener<T extends Event> {

    Logger LOG = LoggerFactory.getLogger(EventListener.class);

    Class<T> getEventType();
    Mono<Void> execute(T event);

    default Mono<Void> handleError(Throwable error) {
        LOG.error("Unable to process " + getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}