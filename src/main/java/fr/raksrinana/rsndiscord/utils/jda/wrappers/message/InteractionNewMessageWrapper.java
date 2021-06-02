package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class InteractionNewMessageWrapper{
	private final ISnowflake target;
	private WebhookMessageAction<Message> action;
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		this.target = target;
		this.action = hook.sendMessageEmbeds(embed);
	}
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		this.target = target;
		this.action = hook.sendMessage(message);
	}
	
	public InteractionNewMessageWrapper ephemeral(boolean state){
		action = action.setEphemeral(state);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Message> submit(){
		return action.submit()
				.thenApply(message -> {
					Logger logger;
					if(target instanceof Guild g){
						logger = Log.getLogger(g);
					}
					else{
						logger = Log.getLogger();
					}
					logger.info("Replied with new message to slash command in {} : {}", target, message.getContentRaw());
					return message;
				});
	}
}
