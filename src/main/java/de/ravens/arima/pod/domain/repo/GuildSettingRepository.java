package de.ravens.arima.pod.domain.repo;


import de.ravens.arima.pod.boundary.ddd.annotations.DomainRepository;
import de.ravens.arima.pod.domain.entity.GuildSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@DomainRepository
public interface GuildSettingRepository extends MongoRepository<GuildSettings, String> {
//    List<GuildSettings> findByServerId(String serverId);
    Optional<GuildSettings> findByServerId(String serverId);
}
