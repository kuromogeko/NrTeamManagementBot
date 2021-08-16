package de.ravens.arima.pod.application.dto;

import de.ravens.arima.pod.domain.entity.Team;
import de.ravens.arima.pod.domain.entity.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TeamDto {
    private String name;
    private String roleId;
    private String guildId;
    private List<TeamMember> members;

    public TeamDto(Team team){
        this.name = team.getName();
        this.roleId = team.getRoleId();
        this.guildId = team.getGuildId();
        this. members = List.copyOf(team.getMembers());
    }

    public static TeamDto mapFromTeam(Team team){
        return new TeamDto(team);
    }

    public static Team mapToTeam(TeamDto dto, String id){
        return new Team(id, dto.getName(),dto.getRoleId(), dto.getGuildId(), List.copyOf(dto.getMembers()));
    }
}
