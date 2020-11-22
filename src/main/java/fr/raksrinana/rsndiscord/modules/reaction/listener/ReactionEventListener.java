package fr.raksrinana.rsndiscord.modules.reaction.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;

@EventListener
public class ReactionEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReactionAdd(@NonNull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		var guild = event.getGuild();
		
		try{
			if(event.getUser().isBot()){
				return;
			}
			var it = Settings.get(guild).getMessagesAwaitingReaction();
			while(it.hasNext()){
				var waitingReactionMessage = it.next();
				if(Objects.equals(waitingReactionMessage.getMessage().getMessageId(), event.getMessageIdLong())
						&& Objects.equals(waitingReactionMessage.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())){
					for(var handler : ReactionUtils.getHandlers()){
						if(handler.acceptTag(waitingReactionMessage.getTag())){
							var result = handler.accept(event, waitingReactionMessage);
							if(result == PROCESSED_DELETE){
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
		catch(final Exception e){
			Log.getLogger(guild).error("", e);
		}
	}
}
