package de.ravens.arima.pod.data.repo;

import de.ravens.arima.pod.data.entitiy.CaptainConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CaptainConfigurationRepository extends MongoRepository<CaptainConfiguration, String> {
    Optional<CaptainConfiguration> findByServerId(String serverId);
}
