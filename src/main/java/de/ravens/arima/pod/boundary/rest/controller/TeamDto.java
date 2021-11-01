package de.ravens.arima.pod.boundary.rest.controller;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDto {
    @Getter
    private String id;
    @Getter
    private String name;

    private List<String> membersWhichAreNotCaptain;
    private String captainId;
    private String roleId;


    public List<Snowflake> getMembersWhichAreNotCaptain() {
        return membersWhichAreNotCaptain.stream().map(Snowflake::of).collect(Collectors.toList());
    }

    public Snowflake getCaptainId() {
        return Snowflake.of(captainId);
    }

    public Snowflake getRoleId() {
        return Snowflake.of(roleId);
    }
}
