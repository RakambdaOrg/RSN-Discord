package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;

public class EditMessageWrapper{
	private final ISnowflake target;
	private final Message message;
	private final MessageAction action;
	
	public EditMessageWrapper(@Nullable ISnowflake target, @NotNull Message message, @NotNull String content){
		this.target = target;
		this.message = message;
		this.action = message.editMessage(content);
	}
	
	public EditMessageWrapper(@Nullable ISnowflake target, @NotNull Message message, @NotNull MessageEmbed embed){
		this.target = target;
		this.message = message;
		this.action = message.editMessage(embed);
	}
	
	@NotNull
	public CompletableFuture<Message> submit(){
		return action.submit()
				.thenApply(m -> {
					Logger logger;
					if(target instanceof Guild g){
						logger = Log.getLogger(g);
					}
					else{
						logger = Log.getLogger();
					}
					
					logger.info("Edited message {} with content: {}", message, message.getContentRaw());
					
					return m;
				});
	}
}
