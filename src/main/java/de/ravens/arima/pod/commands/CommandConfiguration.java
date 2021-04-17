package de.ravens.arima.pod.commands;

import de.ravens.arima.pod.data.entitiy.ServerConfiguration;
import de.ravens.arima.pod.data.repo.ServerConfigurationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter
@Setter
@Component
@AllArgsConstructor
public class CommandConfiguration {
    private final ServerConfigurationRepository serverRepository;

    public ServerConfiguration findOrCreateCommandConfig(String id) {
        return serverRepository.findByServerId(id).stream().findFirst().orElse(createEmptyServerConfiguration(id));
    }

    public ServerConfiguration saveServerConfiguration(ServerConfiguration s) {
        return this.serverRepository.save(s);
    }

    private ServerConfiguration createEmptyServerConfiguration(String id) {
        var config = new ServerConfiguration();
        config.setServerId(id);
        config.setRoles(new ArrayList<>());
        return config;
    }


}
