package de.ravens.arima.pod.domain.team.repo;

import de.ravens.arima.pod.domain.team.things.Team;
import discord4j.common.util.Snowflake;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TeamRepository extends MongoRepository<Team, String> {
    public List<Team> findAllByGuildId(Snowflake guildId);
}
