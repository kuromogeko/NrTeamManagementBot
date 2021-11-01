package de.ravens.arima.pod.boundary.repo.guild;


import de.ravens.arima.pod.domain.guild.things.GuildSettings;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class RGuild {
    @Id
    public String id;
    public GuildSettings settings;

}
