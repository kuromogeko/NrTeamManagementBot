package de.ravens.arima.pod.domain.repo;

import de.ravens.arima.pod.domain.entity.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team, String> {

}
