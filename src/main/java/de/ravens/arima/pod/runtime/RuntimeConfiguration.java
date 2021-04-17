package de.ravens.arima.pod.runtime;

import discord4j.core.GatewayDiscordClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class RuntimeConfiguration implements CommandLineRunner, ApplicationContextAware {
    private final GatewayDiscordClient gatewayDiscordClient;
    private ApplicationContext applicationContext;
    Logger LOG = LoggerFactory.getLogger(RuntimeConfiguration.class);

    @Override
    public void run(String... args) {
        LOG.info("The App will shut down on disconnect of Discord client");
        this.gatewayDiscordClient.onDisconnect().block();
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).close();
        } else {
            System.exit(-1);
        }
    }
}
