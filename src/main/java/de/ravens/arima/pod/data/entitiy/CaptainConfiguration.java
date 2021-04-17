package de.ravens.arima.pod.data.entitiy;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class CaptainConfiguration {
    @Id
    private String id;
    private String serverId;
    private String captainRoleId;
}
