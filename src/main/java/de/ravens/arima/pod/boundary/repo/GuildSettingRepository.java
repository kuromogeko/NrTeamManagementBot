package de.ravens.arima.pod.boundary.repo;


import de.ravens.arima.pod.boundary.ddd.annotations.DomainRepository;
import de.ravens.arima.pod.domain.entity.GuildSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@DomainRepository
public interface GuildSettingRepository extends MongoRepository<GuildSettings, String> {
    List<GuildSettings> findByServerId(String serverId);
}
