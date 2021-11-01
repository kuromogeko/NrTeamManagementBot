package de.ravens.arima.pod.domain.team;

import de.ravens.arima.pod.domain.team.things.Team;

import java.util.List;

public interface TeamUseCase {
    void addTeam(Team team);
    void changeTeam(Team team);
    void removeTeam(String id);

    List<Team> getAllTeams();
    Team getTeam(String id);
}
