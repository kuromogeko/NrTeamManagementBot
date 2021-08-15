package de.ravens.arima.pod.domain.entity;

import de.ravens.arima.pod.boundary.ddd.annotations.DomainEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DomainEntity
public class TeamMember {

    private String discordId;
    private TeamRole role;
}
