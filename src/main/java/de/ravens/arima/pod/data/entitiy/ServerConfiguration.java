package de.ravens.arima.pod.data.entitiy;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class ServerConfiguration {
    @Id
    private String id;
    private String serverId;
    private List<ConfigurationEntity> roles;
}
