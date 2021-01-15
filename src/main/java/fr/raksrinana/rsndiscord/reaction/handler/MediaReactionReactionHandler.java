package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.command.impl.schedule.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.MEDIA_REACTION;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.PACKAGE;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

@ReactionHandler
public class MediaReactionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, MEDIA_REACTION);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == PACKAGE;
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		var guild = event.getGuild();
		var channel = event.getChannel();
		var user = event.retrieveUser().submit().get(30, SECONDS);
		
		return Settings.get(guild)
				.getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> {
					todo.getMessage().getMessage()
							.ifPresent(message -> message.removeReaction(event.getReactionEmote().getEmoji()).queue());
					channel.getManager()
							.setParent(archiveCategory)
							.sync(archiveCategory)
							.submit()
							.thenAccept(future -> channel.sendMessage(translate(guild, "reaction.archived", user.getAsMention())).submit());
					
					ChannelCommand.scheduleDeletion(ZonedDateTime.now().plusWeeks(2), channel, user);
					return PROCESSED_DELETE;
				}).orElseGet(() -> {
					event.getReaction().removeReaction(user).submit();
					channel.sendMessage(translate(guild, "reaction.no-archive")).submit()
							.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusMinutes(5)));
					return PROCESSED;
				});
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
