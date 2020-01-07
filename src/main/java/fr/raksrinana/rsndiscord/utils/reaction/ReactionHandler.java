package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public interface ReactionHandler extends Comparable<ReactionHandler>{
	boolean acceptTag(@NonNull ReactionTag tag);
	
	ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction);
	
	@Override
	default int compareTo(@NonNull ReactionHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
