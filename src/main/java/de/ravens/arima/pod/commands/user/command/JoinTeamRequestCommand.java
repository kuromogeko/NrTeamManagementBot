package de.ravens.arima.pod.commands.user.command;

import de.ravens.arima.pod.data.repo.CaptainConfigurationRepository;
import de.ravens.arima.pod.data.repo.TeamCaptainGuildRepository;
import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.ravens.arima.pod.commands.CommandFilters.getHasCaptainRolePredicate;

@Component
@RequiredArgsConstructor
public class JoinTeamRequestCommand implements EventListener<MessageCreateEvent> {
    public static final String WANNA_JOIN_EMOJI_CODE = "\uD83D\uDC4D";
    public static final String WONT_JOIN_EMOJI_CODE = "\uD83D\uDC4E";
    private final CaptainConfigurationRepository captainConfigurationRepository;
    private final TeamCaptainGuildRepository teamCaptainGuildRepository;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        var guildID = event.getGuildId().orElseThrow();
        return Mono.just(event.getMessage())
                .filter(getHasCaptainRolePredicate(guildID, captainConfigurationRepository))
                .filter(message -> message.getContent().startsWith("!joinRequest"))
                .flatMap(extractRoleAndUserMentions())
                .filter(onlyOneRoleAndUserMentioned())
                .filter(isCaptainOfMentionedTeam(guildID))
                .flatMap(writeTeamInvitation())
                // Thumbs up
                .flatMap(message -> message.addReaction(ReactionEmoji.unicode(WANNA_JOIN_EMOJI_CODE)).thenReturn(message))
                // Thumbs down
                .flatMap(message -> message.addReaction(ReactionEmoji.unicode(WONT_JOIN_EMOJI_CODE)))
                .then();
    }

    @NotNull
    private Function<Tuple3<List<Role>, Set<Snowflake>, Message>, Mono<? extends Message>> writeTeamInvitation() {
        return objects -> {
            var targetTeam = objects.getT1().stream().findFirst().orElseThrow();
            var message = objects.getT3();
            var targetUser = objects.getT2().stream().findFirst().orElseThrow();
            return message.getChannel().flatMap(messageChannel ->
                    messageChannel.createEmbed(embedCreateSpec ->
                            embedCreateSpec.setColor(Color.GREEN)
                                    .setAuthor("Pod", null, null)
                                    .setTitle("Team invitation")
                                    .addField("Inviting Team", targetTeam.getMention(), false)
                                    .addField("Invited User", String.format("<@%s>", targetUser.asString()), false)
                                    .setFooter("Pod Services", null)
                                    .setTimestamp(Instant.now())
                    ));
        };
    }

    @NotNull
    private Function<Message, Mono<? extends Tuple3<List<Role>, Set<Snowflake>, Message>>> extractRoleAndUserMentions() {
        return message ->
                message.getRoleMentions().collectList().map(roles -> Tuples.of(roles, message.getUserMentionIds(), message));
    }

    @NotNull
    private Predicate<Tuple3<List<Role>, Set<Snowflake>, Message>> isCaptainOfMentionedTeam(Snowflake guildID) {
        return objects -> {
            var sourceUser = objects.getT3().getAuthorAsMember();
            var targetTeam = objects.getT1().stream().findFirst().orElseThrow();
            return sourceUser.map(member -> teamCaptainGuildRepository
                    .findByServerIdAndUserIdAndRoleId(guildID.asString(), member.getId().asString(), targetTeam.getId().asString()).isPresent())
                    .blockOptional()
                    .orElse(false);
        };
    }

    @NotNull
    private Predicate<Tuple3<List<Role>, Set<Snowflake>, Message>> onlyOneRoleAndUserMentioned() {
        return objects -> {
            var roles = objects.getT1();
            var usersMentioned = objects.getT2();
            return ((roles.size() == 1) && (usersMentioned.size() == 1));
        };
    }

    @Override
    public Mono<Void> handleError(Throwable error) {
        return null;
    }
}
