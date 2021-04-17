package de.ravens.arima.pod.commands


import discord4j.core.object.entity.Member
import discord4j.core.object.entity.Message
import discord4j.core.object.entity.Role
import discord4j.core.object.entity.User
import discord4j.core.object.entity.channel.MessageChannel
import discord4j.rest.util.PermissionSet
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class CommandFiltersTest extends Specification {
    def "Correctly filters for Message Event, user is bot case"() {
        given:
        def message = mock(Message)
        def channel = Mock(MessageChannel)

        def user = Mock(User)
        when(message.getAuthor()).thenReturn(Optional.of(user))
        when(message.getChannel()).thenReturn(Mono.just(channel))

        when:
        def result = CommandFilters.messageSentByBotPredicate.test(message)

        then:
        !result
        1 * user.isBot() >> true
        0 * channel.createMessage("Test") >> Mono.just(mock(Message))

    }

    def "Only admins can pass the predicate"() {
        given:
        def message = mock(Message)

        def member = mock(Member)
        def permissions = PermissionSet.all()
        def role = mock(Role)

        when(message.getAuthorAsMember()).thenReturn(Mono.just(member))
        when(member.getBasePermissions()).thenReturn(Mono.just(permissions))
        when(member.getRoles()).thenReturn(Flux.just(role))
        when(role.getPermissions()).thenReturn(permissions)

        when:
        def result = CommandFilters.messageSentByAdminPredicate.test(message)

        then:
        result
    }


}
