package fr.raksrinana.rsndiscord.modules.reaction.handler;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public interface IReactionHandler extends Comparable<IReactionHandler>{
	boolean acceptTag(@NonNull ReactionTag tag);
	
	ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction);
	
	@Override
	default int compareTo(@NonNull IReactionHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
