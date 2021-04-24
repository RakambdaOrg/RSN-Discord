package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MessageWrapper{
	private final ISnowflake target;
	private MessageAction action;
	
	public MessageWrapper(@Nullable ISnowflake target, @NotNull MessageChannel channel, @NotNull String message){
		this.target = target;
		this.action = channel.sendMessage(message);
	}
	
	public MessageWrapper(@Nullable ISnowflake target, @NotNull MessageChannel channel, @NotNull MessageEmbed embed){
		this.target = target;
		this.action = channel.sendMessage(embed);
	}
	
	public MessageWrapper(@Nullable ISnowflake target, @NotNull TextChannel channel, @NotNull Message message){
		this.target = target;
		this.action = channel.sendMessage(message);
	}
	
	@NotNull
	public MessageWrapper allowedMentions(@Nullable Collection<Message.MentionType> mentionTypes){
		action = action.allowedMentions(mentionTypes);
		return this;
	}
	
	@NotNull
	public MessageWrapper mention(@NotNull Set<? extends IMentionable> mentions){
		action = action.mention(mentions);
		return this;
	}
	
	@NotNull
	public MessageWrapper mention(@NotNull IMentionable... mentions){
		action = action.mention(mentions);
		return this;
	}
	
	@NotNull
	public MessageWrapper tts(boolean state){
		action = action.tts(state);
		return this;
	}
	
	@NotNull
	public MessageWrapper replyTo(@NotNull Message message){
		action = action.reference(message);
		return this;
	}
	
	@NotNull
	public MessageWrapper addFile(@NotNull Path path){
		action = action.addFile(path.toFile());
		return this;
	}
	
	@NotNull
	public MessageWrapper addFile(@NotNull InputStream inputStream, @NotNull String fileName){
		action = action.addFile(inputStream, fileName);
		return this;
	}
	
	@NotNull
	public MessageWrapper embed(@Nullable MessageEmbed embed){
		action = action.embed(embed);
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
					logger.info("Sent message to {} : {}", target, message.getContentRaw());
					return message;
				});
	}
}
