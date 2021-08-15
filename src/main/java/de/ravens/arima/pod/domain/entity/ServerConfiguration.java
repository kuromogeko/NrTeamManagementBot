package de.ravens.arima.pod.domain.entity;

import de.ravens.arima.pod.boundary.ddd.annotations.DomainEntity;
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
@DomainEntity
public class ServerConfiguration {
    @Id
    private String id;
    private String serverId;
    private List<String> roles;
}
