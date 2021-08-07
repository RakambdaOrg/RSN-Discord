package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.WaitingReactionMessageConfiguration;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public interface IReactionHandler extends Comparable<IReactionHandler>{
	boolean acceptTag(@NotNull ReactionTag tag);
	
	@NotNull
	ReactionHandlerResult accept(@NotNull GuildMessageReactionAddEvent event, @NotNull WaitingReactionMessageConfiguration reaction);
	
	@Override
	default int compareTo(@NotNull IReactionHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
