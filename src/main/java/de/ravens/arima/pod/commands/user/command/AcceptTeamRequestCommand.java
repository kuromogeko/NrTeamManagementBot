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

import static de.ravens.arima.pod.commands.user.command.JoinTeamRequestCommand.WANNA_JOIN_EMOJI_CODE;

@Component
public class AcceptTeamRequestCommand implements EventListener<ReactionAddEvent> {


    private static boolean messageSentByMe(FlowObject object) {
        var message = object.getMessage();
        return message.getAuthorAsMember()
                .map(User::getId)
                .map(snowflake -> (0 == snowflake.compareTo(message.getClient().getSelfId())))
                .blockOptional()
                .orElse(false);
    }

    private static boolean isTeamInvitation(FlowObject flow) {
        return flow.getMessage()
                .getEmbeds()
                .stream()
                .findFirst()
                .map(embed -> embed
                        .getTitle()
                        .orElse("")
                        .equals("Team invitation"))
                .orElse(false);
    }

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> execute(ReactionAddEvent event) {
        return Mono.just(event)
                .filter(this::getMessageWasHappyEmojiPredicate)
                .flatMap(this::addMessageToContext)
                .filter(AcceptTeamRequestCommand::isTeamInvitation)
                .filter(AcceptTeamRequestCommand::messageSentByMe)
                .filter(this::getReactionWasByMentionedUserPredicate)
                .flatMap(this::addUserToContext)
                .flatMap(this::addRoleToUserAndReturn)
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
                        .setContent(String.format("%sInvitation accepted by <@%s>", objects.getAdditionalMessage(), objects.getMember().getId().asString()))
                .setEmbed(embedCreateSpec -> embedCreateSpec.setTitle("Invitation accepted")));
    }

    @NotNull
    private Mono<FlowObject> addRoleToUserAndReturn(FlowObject objects) {
        var embed = objects.getMessage().getEmbeds().stream().findFirst().orElseThrow();
        var teamField = embed.getFields().stream()
                .filter(field -> field.getName().equals("Inviting Team"))
                .findFirst()
                .orElseThrow();

        var roleFlake = Snowflake.of(teamField.getValue()
                .replace("<@&", "")
                .replace(">", ""));

        objects.setAdditionalMessage("");

        return objects.getMember().addRole(roleFlake).doOnError(throwable -> {
            objects.setAdditionalMessage("Error setting team role, please contact a Mod to do so. ");
        }).thenReturn(objects);
    }

    @NotNull
    private Mono<FlowObject> addUserToContext(FlowObject flow) {
        Message message = flow.getMessage();
        Snowflake guildId = message.getGuildId().orElseThrow();
        GatewayDiscordClient client = message.getClient();
        var embed = message.getEmbeds().stream().findFirst().orElseThrow();
        var userInvitationField = embed
                .getFields()
                .stream()
                .filter(field -> field.getName().equals("Invited User"))
                .findFirst()
                .orElseThrow();

        var mentionedUserId = Snowflake.of(userInvitationField.getValue()
                .replace("<@", "")
                .replace(">", ""));
        return client.getMemberById(guildId, mentionedUserId)
                .map(member -> {
                    flow.setMember(member);
                    return flow;
                });
    }


    private boolean getReactionWasByMentionedUserPredicate(FlowObject flow) {
        var embed = flow.getMessage().getEmbeds().stream().findFirst().orElseThrow();
        var userInvitationField = embed
                .getFields()
                .stream()
                .filter(field -> field.getName().equals("Invited User"))
                .findFirst()
                .orElseThrow();

        var mentionedUserId = Snowflake.of(userInvitationField.getValue()
                .replace("<@", "")
                .replace(">", ""));
        var member = flow.getEvent().getMember().orElseThrow();
        return (0 == member.getId().compareTo(mentionedUserId));
    }

    private boolean getMessageWasHappyEmojiPredicate(ReactionAddEvent event) {
        var maybeEmoji = event.getEmoji().asUnicodeEmoji();
        if (maybeEmoji.isEmpty()) {
            return false;
        }
        return maybeEmoji.get().getRaw().equals(WANNA_JOIN_EMOJI_CODE);
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
        String additionalMessage;
    }
}
