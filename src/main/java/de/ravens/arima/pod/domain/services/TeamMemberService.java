package de.ravens.arima.pod.domain.services;

import de.ravens.arima.pod.boundary.ddd.annotations.DomainService;
import de.ravens.arima.pod.domain.entity.Team;
import de.ravens.arima.pod.domain.entity.TeamMember;
import de.ravens.arima.pod.domain.entity.TeamRole;
import discord4j.core.object.entity.Guild;
import org.springframework.stereotype.Service;

@Service
@DomainService
public class TeamMemberService {

    //TODO delete

    /**
     * @param team target team
     * @param discordId target discord user
     * @param role target role for teammember
     * @return new team, not saved yet
     */
    public Team addTeamMemberToTeam(Team team, String discordId, TeamRole role, Guild guild){
        if(!teamMemberIsInGuild(guild, discordId)){
            throw new RuntimeException("User is not in Guild this team is registered for");
        }
        if(teamMemberIsInTeam(team, discordId)){
            throw new RuntimeException("Member is already in Team");
        }
        if(!roleIsValid(team, role)){
            throw new RuntimeException("Target role for user is taken");
        }
        team.getMembers().add(new TeamMember(discordId, role));
        return team;
    }

    /**
     * @param team to check for
     * @param role target role
     * @return wether role is available or unlimited
     */
    private boolean roleIsValid(Team team, TeamRole role) {
        if(role == TeamRole.MEMBER){
            return true;
        }
        return team.getMembers().stream().anyMatch(teamMember -> teamMember.getRole().equals(role));
    }

    private boolean teamMemberIsInTeam(Team team, String discordId) {
        return team.getMembers().stream()
                .anyMatch(teamMember -> teamMember.getDiscordId().equals(discordId));
    }

    private boolean teamMemberIsInGuild(Guild guild, String discordId){
        return guild.getMembers().any(member -> member.getId().asString().equals(discordId)).blockOptional().orElseThrow();
    }


    /**
     * @param team team to work with
     * @param discordId id of user to work with
     * @param role role of user
     * @return new Team, not saved in repo yet
     */
    public Team updateTeamMembersRole(Team team, String discordId, TeamRole role){
        if(!teamMemberIsInTeam(team, discordId)){
            throw new RuntimeException("The Member is not in the team");
        }
        if(!roleIsValid(team, role)){
            throw new RuntimeException("The Member may not take the selected role");
        }
        var member = team
                .getMembers()
                .stream()
                .filter(teamMember -> teamMember.getDiscordId().equals(discordId))
                .findFirst()
                .orElseThrow();
        member.setRole(role);
        return team;
    }


}
