package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
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
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo){
		var guild = event.getGuild();
		
		return todo.getMessage().getMessage()
				.map(message -> {
					var channel = message.getTextChannel();
					var guildConfiguration = Settings.get(guild);
					
					guildConfiguration.getArchiveCategory()
							.flatMap(CategoryConfiguration::getCategory)
							.ifPresentOrElse(archiveCategory -> {
										JDAWrappers.edit(channel)
												.setParent(archiveCategory)
												.sync(archiveCategory)
												.submit()
												.thenAccept(future -> JDAWrappers.message(channel, translate(guild, "reaction.archived", event.getMember().getAsMention())).submit());
										
										guildConfiguration.add(new DeleteChannelScheduleActionHandler(channel.getIdLong(), ZonedDateTime.now().plusDays(4)));
									},
									() -> JDAWrappers.delete(channel).submit());
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
