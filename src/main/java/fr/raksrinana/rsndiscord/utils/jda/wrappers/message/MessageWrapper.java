package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.schedule.ScheduleService.deleteMessageMins;

@Log4j2
public class MessageWrapper extends ActionWrapper<Message, MessageAction>{
	private final MessageChannel channel;
	
	public MessageWrapper(@NotNull MessageChannel channel, @NotNull String message){
		super(channel.sendMessage(message));
		this.channel = channel;
	}
	
	public MessageWrapper(@NotNull MessageChannel channel, @NotNull MessageEmbed embed){
		super(channel.sendMessageEmbeds(embed));
		this.channel = channel;
	}
	
	public MessageWrapper(@NotNull MessageChannel channel, @NotNull Message message){
		super(channel.sendMessage(message));
		this.channel = channel;
	}
	
	@NotNull
	public MessageWrapper allowedMentions(@Nullable Collection<Message.MentionType> mentionTypes){
		getAction().allowedMentions(mentionTypes);
		return this;
	}
	
	@NotNull
	public MessageWrapper mention(@NotNull Set<? extends IMentionable> mentions){
		getAction().mention(mentions);
		return this;
	}
	
	@NotNull
	public MessageWrapper mention(@NotNull IMentionable... mentions){
		getAction().mention(mentions);
		return this;
	}
	
	@NotNull
	public MessageWrapper tts(boolean state){
		getAction().tts(state);
		return this;
	}
	
	@NotNull
	public MessageWrapper replyTo(@NotNull Message message){
		getAction().reference(message);
		return this;
	}
	
	@NotNull
	public MessageWrapper replyTo(@NotNull MessageReference messageReference){
		getAction().referenceById(messageReference.getMessageIdLong());
		return this;
	}
	
	@NotNull
	public MessageWrapper addFile(@NotNull Path path){
		getAction().addFile(path.toFile());
		return this;
	}
	
	@NotNull
	public MessageWrapper addFile(@NotNull InputStream inputStream, @NotNull String fileName){
		getAction().addFile(inputStream, fileName);
		return this;
	}
	
	@NotNull
	public MessageWrapper embed(@Nullable MessageEmbed embed){
		getAction().setEmbeds(embed);
		return this;
	}
	
	@NotNull
	public MessageWrapper addActionRow(@NotNull Collection<ItemComponent> components){
		getAction().setActionRow(components);
		return this;
	}
	
	@NotNull
	public MessageWrapper addActionRow(@NotNull ItemComponent... components){
		getAction().setActionRow(components);
		return this;
	}
	
	@NotNull
	public MessageWrapper clearActionRows(){
		getAction().setActionRows(List.of());
		return this;
	}
	
	@NotNull
	public MessageWrapper content(String content){
		getAction().content(content);
		return this;
	}
	
	@NotNull
	public MessageWrapper setActionRows(@NotNull ActionRow... actionRows){
		getAction().setActionRows(actionRows);
		return this;
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Sent message to {} : {}", channel, value.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to send message to channel {}", channel, throwable);
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(@NotNull Duration duration){
		return submitAndDelete(duration.toMinutes());
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(long minutes){
		return submit().thenApply(deleteMessageMins(minutes));
	}
}
