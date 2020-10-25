package fr.raksrinana.rsndiscord.modules.reaction.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;

@EventListener
public class ReactionEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReactionAdd(@NonNull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			if(!event.getUser().isBot()){
				final var it = Settings.get(event.getGuild()).getMessagesAwaitingReaction();
				while(it.hasNext()){
					final var waitingReactionMessage = it.next();
					if(Objects.equals(waitingReactionMessage.getMessage().getMessageId(), event.getMessageIdLong()) && Objects.equals(waitingReactionMessage.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())){
						for(final var handler : ReactionUtils.getHandlers()){
							if(handler.acceptTag(waitingReactionMessage.getTag())){
								final var result = handler.accept(event, waitingReactionMessage);
								if(result == ReactionHandlerResult.PROCESSED_DELETE){
									it.remove();
								}
								if(result.isTerminal()){
									break;
								}
							}
						}
					}
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
