package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;

@EventListener
@Log4j2
public class ReactionEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		
		var guild = event.getGuild();
		var user = event.getUser();
		
		try(var context = LogContext.with(guild).with(user)){
			if(user.isBot()){
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
		catch(Exception e){
			log.error("", e);
		}
	}
}
