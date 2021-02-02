package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.command.impl.schedule.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

@ReactionHandler
public class ChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NotNull BasicEmotes emote){
		return CROSS_NO == emote;
	}
	
	@Override
	@NotNull
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		var guild = event.getGuild();
		var user = event.retrieveUser().submit().get(30, SECONDS);
		
		return todo.getMessage().getMessage()
				.map(message -> {
					var channel = message.getTextChannel();
					Settings.get(guild).getArchiveCategory()
							.flatMap(CategoryConfiguration::getCategory)
							.ifPresentOrElse(archiveCategory -> {
										channel.getManager().setParent(archiveCategory)
												.sync(archiveCategory)
												.submit()
												.thenAccept(future -> channel.sendMessage(translate(guild, "reaction.archived", event.getMember().getAsMention())).submit());
										ChannelCommand.scheduleDeletion(ZonedDateTime.now().plusDays(4), channel, user);
									},
									() -> channel.delete().submit());
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
