package de.ravens.arima.pod.commands.user.command;

import de.ravens.arima.pod.event.EventListener;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static de.ravens.arima.pod.commands.user.command.JoinTeamRequestCommand.WONT_JOIN_EMOJI_CODE;

//TODO Simplify - many methods of this are the same as accept

@Component
public class DenyTeamRequestCommand implements EventListener<ReactionAddEvent> {

    private static boolean messageSentByMe(FlowObject object) {
        var message = object.getMessage();
        return message.getAuthorAsMember()
                .map(User::getId)
                .map(snowflake -> (0 == snowflake.compareTo(message.getClient().getSelfId())))
                .blockOptional()
                .orElse(false);
    }

    private static boolean isTeamInvitation(FlowObject flow) {
        return flow.getMessage().getContent().contains("Team Invitation");
    }

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> execute(ReactionAddEvent event) {
        return Mono.just(event)
                .filter(this::getMessageWasNegativeEmojiPredicate)
                .flatMap(this::addMessageToContext)
                .filter(this::getReactionWasByMentionedUserPredicate)
                .filter(DenyTeamRequestCommand::messageSentByMe)
                .filter(DenyTeamRequestCommand::isTeamInvitation)
                .flatMap(this::addUserToContext)
                .flatMap(this::editOldMessage)
                .then();
    }

    private Mono<FlowObject> addMessageToContext(ReactionAddEvent reactionAddEvent) {
        return reactionAddEvent.getMessage()
                .map(message -> FlowObject.builder()
                        .message(message)
                        .event(reactionAddEvent)
                        .build());
    }

    @NotNull
    private Mono<Message> editOldMessage(FlowObject objects) {
        return objects.getMessage()
                .edit(messageEditSpec -> messageEditSpec
                        .setContent(String.format("Invitation denied by <@%s>", objects.getMember().getId().asString())));
    }

    @NotNull
    private Mono<FlowObject> addUserToContext(FlowObject flow) {
        Message message = flow.getMessage();
        Snowflake guildId = message.getGuildId().orElseThrow();
        GatewayDiscordClient client = message.getClient();
        var userToBeAdded = message.getUserMentionIds().stream().findFirst().orElseThrow();
        return client.getMemberById(guildId, userToBeAdded)
                .map(member -> {
                    flow.setMember(member);
                    return flow;
                });
    }


    private boolean getReactionWasByMentionedUserPredicate(FlowObject flow) {
        var mentionedUserId = flow.getMessage().getUserMentionIds().stream().findFirst().orElseThrow();
        var member = flow.getEvent().getMember().orElseThrow();
        return (0 == member.getId().compareTo(mentionedUserId));
    }

    private boolean getMessageWasNegativeEmojiPredicate(ReactionAddEvent event) {
        var maybeEmoji = event.getEmoji().asUnicodeEmoji();
        if (maybeEmoji.isEmpty()) {
            return false;
        }
        return maybeEmoji.get().getRaw().equals(WONT_JOIN_EMOJI_CODE);
    }


    @Override
    public Mono<Void> handleError(Throwable error) {
        return null;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    private static class FlowObject {
        ReactionAddEvent event;
        Message message;
        Member member;
    }
}
