package de.ravens.arima.pod.data.repo;


import de.ravens.arima.pod.data.entitiy.ServerConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServerConfigurationRepository extends MongoRepository<ServerConfiguration, String> {
    List<ServerConfiguration> findByServerId(String serverId);
}
