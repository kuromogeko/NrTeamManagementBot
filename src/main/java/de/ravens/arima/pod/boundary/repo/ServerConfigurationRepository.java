package de.ravens.arima.pod.boundary.repo;


import de.ravens.arima.pod.boundary.ddd.annotations.DomainRepository;
import de.ravens.arima.pod.domain.entity.ServerConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@DomainRepository
public interface ServerConfigurationRepository extends MongoRepository<ServerConfiguration, String> {
    List<ServerConfiguration> findByServerId(String serverId);
}
