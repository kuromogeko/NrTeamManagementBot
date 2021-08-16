package de.ravens.arima.pod.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class InfoDto {
    private String name;
    private String discordId;
    private List<String> scopes;
}
