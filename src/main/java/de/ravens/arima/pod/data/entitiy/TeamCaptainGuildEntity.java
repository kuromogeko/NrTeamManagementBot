package de.ravens.arima.pod.data.entitiy;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document
public class TeamCaptainGuildEntity {
    @Id
    private String id;

    private String serverId;
    private String userId;
    private String roleId;
}
