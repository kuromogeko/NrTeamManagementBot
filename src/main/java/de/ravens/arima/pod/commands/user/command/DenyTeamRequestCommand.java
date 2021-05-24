package de.ravens.arima.pod.commands.user.command;

import de.ravens.arima.pod.commands.user.command.AcceptTeamRequestCommand.FlowObject;
import de.ravens.arima.pod.event.EventListener;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static de.ravens.arima.pod.commands.user.command.JoinTeamRequestCommand.WONT_JOIN_EMOJI_CODE;


@Component
public class DenyTeamRequestCommand implements EventListener<ReactionAddEvent> {

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> execute(ReactionAddEvent event) {
        return Mono.just(event)
                .filter(this::getMessageWasNegativeEmojiPredicate)
                .flatMap(AcceptTeamRequestCommand::addMessageToContext)
                .filter(AcceptTeamRequestCommand::isTeamInvitation)
                .filter(AcceptTeamRequestCommand::messageSentByMe)
                .filter(AcceptTeamRequestCommand::getReactionWasByMentionedUserPredicate)
                .flatMap(AcceptTeamRequestCommand::addUserToContext)
                .flatMap(this::editOldMessage)
                .then();
    }

    @NotNull
    private Mono<Message> editOldMessage(FlowObject objects) {
        return objects.getMessage()
                .edit(messageEditSpec -> messageEditSpec
                        .setContent(String.format("Invitation denied by <@%s>", objects.getMember().getId().asString()))
                        .setEmbed(embedCreateSpec -> embedCreateSpec.setTitle("Invitation denied")));
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


}
