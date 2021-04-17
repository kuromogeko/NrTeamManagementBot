package de.ravens.arima.pod.commands.admin.command

//import de.ravens.arima.pod.commands.CommandConfiguration
//import de.ravens.arima.pod.commands.CommandFilters
//import de.ravens.arima.pod.commands.util.DisplayUtil
//import de.ravens.arima.pod.data.entitiy.CaptainConfiguration
//import de.ravens.arima.pod.data.entitiy.TeamCaptainGuildEntity
//import de.ravens.arima.pod.data.repo.CaptainConfigurationRepository
//import de.ravens.arima.pod.data.repo.TeamCaptainGuildRepository
//import discord4j.common.util.Snowflake
//import discord4j.core.GatewayDiscordClient
//import discord4j.core.event.domain.message.MessageCreateEvent
//import discord4j.core.object.entity.Member
//import discord4j.core.object.entity.Message
//import discord4j.core.object.entity.Role
//import discord4j.core.object.entity.channel.MessageChannel
//import org.mockito.MockedStatic
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//import reactor.util.function.Tuples
//import spock.lang.Shared
import spock.lang.Specification

//import java.util.function.Predicate
//
//import static org.mockito.ArgumentMatchers.any
//import static org.mockito.Mockito.*

class SetCaptainWithRoleCommandTest extends Specification {
//    @Shared
//    GatewayDiscordClient client
//    @Shared
//    Snowflake guildId
//
//    @Shared
//    MessageChannel messageChannel
//    @Shared
//    List<Role> roles
//    @Shared
//    CaptainConfigurationRepository captainConfigurationRepository
//    @Shared
//    TeamCaptainGuildRepository teamCaptainGuildRepository
//    @Shared
//    Member memberForUser
//
//    @Shared
//    Optional<CaptainConfiguration> optConfigObj
//    @Shared
//    MockedStatic<Snowflake> staticSnowflake
//
//    @SuppressWarnings('unused')
//    void setupSpec() {
//        client = mock(GatewayDiscordClient)
//        guildId = mock(Snowflake)
//        messageChannel = mock(MessageChannel)
//        roles = List.<Role> of()
//        captainConfigurationRepository = mock(CaptainConfigurationRepository)
//        teamCaptainGuildRepository = mock(TeamCaptainGuildRepository)
//        memberForUser = mock(Member)
//        optConfigObj = Optional.of(new CaptainConfiguration("1", "guildid", "capid"))
//        staticSnowflake = mockStatic(Snowflake.class)
//    }
//
//    def "Sets captain role to mentioned Users"() {
//        Snowflake userToRole = mock(Snowflake)
//        Set<Snowflake> users = Set.of(userToRole)
//        staticSnowflake
//                .when({ -> Snowflake.of("capid") })
//                .thenReturn(mock(Snowflake))
//        when(guildId.asString()).thenReturn("guildid")
//        when(captainConfigurationRepository.findByServerId("guildid")).thenReturn(optConfigObj)
//
//        when(client.getMemberById(guildId, userToRole)).thenReturn(Mono.just(memberForUser))
//        when(memberForUser.addRole(any())).thenReturn(Mono.empty().then())
//
//        def parent = new SetCaptainWithRoleCommand(null, null, captainConfigurationRepository)
//        //noinspection GroovyAccessibility
//        def sut = parent.setCaptainRoleToUsers(guildId, client)
//        def input = Tuples.of(users, roles, messageChannel)
//        def result = sut.apply(input).block()
//
//        expect:
//        users == result.getT1() // user
//        roles == result.getT2() // roles
//        messageChannel == result.getT3() //channel
//        "Added captain role  <@&capid> for Users" == result.getT4() //Message
//    }
//
//    def "Immediately returns on no captain role"() {
//        Snowflake userToRole = mock(Snowflake)
//        Set<Snowflake> users = Set.of(userToRole)
//
//        when(guildId.asString()).thenReturn("guildid")
//        when(captainConfigurationRepository.findByServerId("guildid")).thenReturn(Optional.empty())
//
//        def parent = new SetCaptainWithRoleCommand(null, null, captainConfigurationRepository)
//        //noinspection GroovyAccessibility
//        def sut = parent.setCaptainRoleToUsers(guildId, client)
//        def input = Tuples.of(users, roles, messageChannel)
//        def result = sut.apply(input).block()
//
//        expect:
//        users == result.getT1() // user
//        roles == result.getT2() // roles
//        messageChannel == result.getT3() //channel
//        "No Captain role configured, skipped setting the role" == result.getT4() //Message
//    }
//
//    def "Will persist the captains role"() {
//        def teamRoleMock = mock(Role)
//        def user = mock(Snowflake)
//        def teamRoleId = mock(Snowflake)
//        def userSet = Set.of(user)
//        def roleList = [teamRoleMock]
//
//        def expectedToSave = new TeamCaptainGuildEntity(null, "guildid", "userid", "teamroleid")
//
//        when(guildId.asString()).thenReturn("guildid")
//        when(user.asString()).thenReturn("userid")
//        when(teamRoleMock.getId()).thenReturn(teamRoleId)
//        when(teamRoleId.asString()).thenReturn("teamroleid")
//        when(teamCaptainGuildRepository.findByServerIdAndUserIdAndRoleId("guildid", "userid", "teamroleid")).thenReturn(Optional.empty())
//        when(teamCaptainGuildRepository.save(expectedToSave)).thenReturn(expectedToSave)
//
//        def parent = new SetCaptainWithRoleCommand(null, teamCaptainGuildRepository, null)
//        //noinspection GroovyAccessibility
//        def sut = parent.persistCaptainRoleRelations(guildId)
//        def input = Tuples.of(userSet, roleList, messageChannel, "any")
//        def result = sut.apply(input)
//
//        expect:
//        result.getT1() == [expectedToSave]
//        result.getT2() == messageChannel
//        result.getT3() == "any"
//    }
//
//    def "gets captain and role mentions"() {
//        def role = mock(Role)
//
//        def parent = new SetCaptainWithRoleCommand(null, null, null)
//        //noinspection GroovyAccessibility
//        def sut = parent.getCaptainAndRoleMentions()
//        def input = mock(Message)
//
//        when(input.getUserMentionIds()).thenReturn(Set.of())
//        when(input.getRoleMentions()).thenReturn(Flux.just(role))
//        when(input.getChannel()).thenReturn(Mono.just(messageChannel))
//
//        def result = sut.apply(input).block()
//
//        expect:
//        result.getT1().empty
//        result.getT2() == [role]
//        result.getT3() == messageChannel
//    }

}
