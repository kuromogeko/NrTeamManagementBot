package de.ravens.arima.pod.security;

import lombok.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class CustomDiscordUserPrincipal  implements Principal {
    private final List<String> scopes;
    private final String name;
    private final String expires;
}
