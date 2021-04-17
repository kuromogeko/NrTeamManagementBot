package de.ravens.arima.pod.data.repo;

import de.ravens.arima.pod.data.entitiy.TeamCaptainGuildEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeamCaptainGuildRepository extends MongoRepository<TeamCaptainGuildEntity, String> {
    Optional<TeamCaptainGuildEntity> findByServerIdAndUserIdAndRoleId(String serverId, String userId, String roleId);
}
