package de.ravens.arima.pod.commands.admin.config


import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.object.entity.Member
import discord4j.core.object.entity.Message
import discord4j.core.object.entity.Role
import discord4j.core.object.entity.User
import discord4j.core.object.entity.channel.MessageChannel
import discord4j.rest.util.PermissionSet
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Ignore
import spock.lang.Specification

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ConfigureAdminRoleCommandTest extends Specification {
    @Ignore
    def "Takes proper action if predicates are passed"() {
        given:
        def subject = new ConfigureAdminRoleCommand()
        def ot = Mock(MessageCreateEvent)
        def message = mock(Message)

        def user = Mock(User)
        def member = mock(Member)
        def permissions = PermissionSet.all()
        def role = mock(Role)
        def channel = Mock(MessageChannel)

        when(message.getAuthor()).thenReturn(Optional.of(user))
        when(message.getAuthorAsMember()).thenReturn(Mono.just(member))
        when(message.getContent()).thenReturn("!test")
        when(message.getChannel()).thenReturn(Mono.just(channel))

        when(member.getBasePermissions()).thenReturn(Mono.just(permissions))
        when(member.getRoles()).thenReturn(Flux.just(role))
        when(role.getPermissions()).thenReturn(permissions)


        when:
        subject.execute(ot).block()

        then:
        1 * ot.getMessage() >> message
        1 * user.isBot() >> false
        1 * channel.createMessage("Test") >> Mono.just(mock(Message))

    }

}
