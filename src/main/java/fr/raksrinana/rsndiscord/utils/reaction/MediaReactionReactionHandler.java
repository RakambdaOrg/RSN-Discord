package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
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
	protected void processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration todo){
		Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(message -> message.removeReaction(event.getReactionEmote().getEmoji()).queue());
		Settings.get(event.getGuild()).getArchiveCategory().flatMap(CategoryConfiguration::getCategory).ifPresent(archiveCategory -> Actions.setCategoryAndSync(event.getChannel(), archiveCategory));
	}
}
