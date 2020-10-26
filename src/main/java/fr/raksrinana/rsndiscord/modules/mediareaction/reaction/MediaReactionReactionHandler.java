package fr.raksrinana.rsndiscord.modules.mediareaction.reaction;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.modules.reaction.handler.TodoReactionHandler;
import fr.raksrinana.rsndiscord.modules.schedule.command.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@ReactionHandler
public class MediaReactionReactionHandler extends TodoReactionHandler {
    @Override
    public boolean acceptTag(@NonNull ReactionTag tag) {
        return Objects.equals(tag, ReactionTag.MEDIA_REACTION);
    }

    @Override
    protected boolean isValidEmote(@NonNull BasicEmotes emote) {
        return emote == BasicEmotes.PACKAGE;
    }

    @Override
    protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException {
        return event.retrieveUser().submit()
                .thenApply(user -> Settings.get(event.getGuild())
                        .getArchiveCategory()
                        .flatMap(CategoryConfiguration::getCategory)
                        .map(archiveCategory -> {
                            todo.getMessage()
                                    .getMessage()
                                    .ifPresent(message -> message.removeReaction(event.getReactionEmote().getEmoji()).queue());
                            Actions.setCategoryAndSync(event.getChannel(), archiveCategory)
                                    .thenAccept(future -> Actions.sendMessage(event.getChannel(), translate(event.getGuild(), "reaction.archived", user.getAsMention()), null));
                            ChannelCommand.scheduleDeletion(ZonedDateTime.now().plusWeeks(2), event.getChannel(), user);
                            return ReactionHandlerResult.PROCESSED_DELETE;
                        }).orElseGet(() -> {
                            Actions.removeReaction(event.getReaction(), user);
                            Actions.reply(event, translate(event.getGuild(), "reaction.no-archive"), null);
                            return ReactionHandlerResult.PROCESSED_DELETE;
                        }))
                .get(30, TimeUnit.SECONDS);
    }

    @Override
    public int getPriority() {
        return 990;
    }
}
