package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.text.MessageFormat;
import java.util.Objects;

public class MediaReactionReactionHandler extends TodosReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.MEDIA_REACTION);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == BasicEmotes.PACKAGE;
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo){
		return Settings.get(event.getGuild()).getArchiveCategory().flatMap(CategoryConfiguration::getCategory).map(archiveCategory -> {
			Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(message -> message.removeReaction(event.getReactionEmote().getEmoji()).queue());
			Actions.setCategoryAndSync(event.getChannel(), archiveCategory).thenAccept(future -> Actions.sendMessage(event.getChannel(), MessageFormat.format("{0} archived this channel.", event.getMember().getAsMention()), null));
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElseGet(() -> {
			Actions.removeReaction(event.getReaction(), event.getUser());
			Actions.reply(event, "No archive category have been defined", null);
			return ReactionHandlerResult.PROCESSED_DELETE;
		});
	}
}
