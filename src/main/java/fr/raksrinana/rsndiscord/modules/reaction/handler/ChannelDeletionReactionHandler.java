package fr.raksrinana.rsndiscord.modules.reaction.handler;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.command.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

@ReactionHandler
public class ChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return CROSS_NO == emote;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
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
