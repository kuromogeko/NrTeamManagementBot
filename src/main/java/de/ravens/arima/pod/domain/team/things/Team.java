package de.ravens.arima.pod.domain.team.things;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Team {
    @Id
    @Getter
    private String id;
    @Getter
    private final Snowflake guildId;
    @Getter
    private String name;

    private List<Snowflake> membersWhichAreNotCaptain;
    @Getter
    private Snowflake captainId;

    @Getter
    private Snowflake roleId;

    public List<Snowflake> getMembers() {
        List<Snowflake> copyList = new ArrayList<>(this.membersWhichAreNotCaptain);
        copyList.add(this.captainId);
        return copyList;
    }


}
