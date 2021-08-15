package de.ravens.arima.pod.boundary.discord.core;

import de.ravens.arima.pod.application.event.EventListener;
import de.ravens.arima.pod.application.event.EventListenerRegistrator;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SetupHandler {
//    Logger LOG = LoggerFactory.getLogger(SetupHandler.class);

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> listenerList) {
        final String token = TokenProvider.provideToken();
        final GatewayDiscordClient client =
                LoginHandler.login(DiscordClient.create(token)).block();
        EventListenerRegistrator.registerEvents(client, listenerList);
        return client;
    }
}
