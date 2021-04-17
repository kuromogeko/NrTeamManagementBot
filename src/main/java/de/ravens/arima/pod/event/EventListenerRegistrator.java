package de.ravens.arima.pod.event;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;

import java.util.List;

public class EventListenerRegistrator {
    public static <T extends Event> void registerEvents(GatewayDiscordClient client, List<EventListener<T>> eventListeners) {
        for (EventListener<T> eventListener : eventListeners) {
            client.on(eventListener.getEventType())
                    .flatMap(eventListener::execute)
                    .onErrorResume(eventListener::handleError)
                    .subscribe();
        }
    }
}
