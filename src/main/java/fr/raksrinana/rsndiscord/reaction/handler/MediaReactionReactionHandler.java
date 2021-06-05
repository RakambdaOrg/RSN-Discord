package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.scheduleaction.impl.DeleteChannelScheduleActionHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
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
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, MEDIA_REACTION);
	}
	
	@Override
	protected boolean isValidEmote(@NotNull BasicEmotes emote){
		return emote == PACKAGE;
	}
	
	@Override
	@NotNull
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emotes, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		var guild = event.getGuild();
		var channel = event.getChannel();
		var user = event.retrieveUser().submit().get(30, SECONDS);
		
		var guildConfiguration = Settings.get(guild);
		return guildConfiguration
				.getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> {
					todo.getMessage().getMessage()
							.ifPresent(message -> JDAWrappers.removeReaction(message, event.getReactionEmote().getEmoji()).submit());
					JDAWrappers.edit(channel)
							.setParent(archiveCategory)
							.sync(archiveCategory)
							.submit()
							.thenAccept(future -> JDAWrappers.message(channel, translate(guild, "reaction.archived", user.getAsMention())).submit());
					
					guildConfiguration.add(new DeleteChannelScheduleActionHandler(channel.getIdLong(), ZonedDateTime.now().plusWeeks(2)));
					return PROCESSED_DELETE;
				}).orElseGet(() -> {
					JDAWrappers.removeReaction(event.getReaction(), user).submit();
					JDAWrappers.message(channel, translate(guild, "reaction.no-archive")).submitAndDelete(5);
					return PROCESSED;
				});
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
