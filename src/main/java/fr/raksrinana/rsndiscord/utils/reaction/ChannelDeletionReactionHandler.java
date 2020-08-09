package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.commands.schedule.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
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

public class ChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return BasicEmotes.CROSS_NO == emote;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		return event.retrieveUser().submit()
				.thenApply(user -> todo.getMessage()
						.getMessage()
						.map(message -> {
							Settings.get(event.getGuild())
									.getArchiveCategory()
									.flatMap(CategoryConfiguration::getCategory)
									.ifPresentOrElse(archiveCategory -> {
												Actions.setCategoryAndSync(message.getTextChannel(), archiveCategory)
														.thenAccept(future -> Actions.sendMessage(message.getTextChannel(), translate(event.getGuild(), "reaction.archived", event.getMember().getAsMention()), null));
												ChannelCommand.scheduleDeletion(ZonedDateTime.now().plusDays(4), message.getTextChannel(), event.getUser());
											},
											() -> Actions.deleteChannel(message.getTextChannel()));
							return ReactionHandlerResult.PROCESSED_DELETE;
						}).orElse(ReactionHandlerResult.PROCESSED))
				.get(30, TimeUnit.SECONDS);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
