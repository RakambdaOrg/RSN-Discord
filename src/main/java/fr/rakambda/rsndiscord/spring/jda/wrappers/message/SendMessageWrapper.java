package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
public class SendMessageWrapper extends MessageWrapper<MessageCreateAction>{
	private final MessageChannel channel;
	
	public SendMessageWrapper(@NonNull MessageChannel channel, @NonNull String message){
		super(channel.sendMessage(message));
		this.channel = channel;
	}
	
	public SendMessageWrapper(@NonNull MessageChannel channel, @NonNull MessageEmbed embed){
		super(channel.sendMessageEmbeds(embed));
		this.channel = channel;
	}
	
	public SendMessageWrapper(@NonNull MessageChannel channel, @NonNull Message message){
		super(channel.sendMessage(MessageCreateBuilder.fromMessage(message).build()));
		this.channel = channel;
	}
	
	public SendMessageWrapper(@NonNull MessageChannel channel, @NonNull MessageTopLevelComponent component, @NonNull MessageTopLevelComponent... components){
		super(channel.sendMessageComponents(component, components));
		this.channel = channel;
	}
	
	@NonNull
	public SendMessageWrapper allowedMentions(@Nullable Collection<Message.MentionType> mentionTypes){
		getAction().setAllowedMentions(mentionTypes);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper mention(@NonNull Set<? extends IMentionable> mentions){
		getAction().mention(mentions);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper mention(@NonNull IMentionable... mentions){
		getAction().mention(mentions);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper tts(boolean state){
		getAction().setTTS(state);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper replyTo(@NonNull Message message){
		getAction().setMessageReference(message);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper replyTo(@NonNull MessageReference messageReference){
		getAction().setMessageReference(messageReference.getMessageIdLong());
		return this;
	}
	
	@NonNull
	public SendMessageWrapper addFile(@NonNull Path path){
		getAction().addFiles(FileUpload.fromData(path));
		return this;
	}
	
	@NonNull
	public SendMessageWrapper addFile(@NonNull InputStream inputStream, @NonNull String fileName){
		getAction().addFiles(FileUpload.fromData(inputStream, fileName));
		return this;
	}
	
	@NonNull
	public SendMessageWrapper embed(@Nullable MessageEmbed embed){
		getAction().setEmbeds(embed);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper addActionRow(@NonNull Collection<MessageTopLevelComponent> components){
		getAction().addComponents(components);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper addActionRow(@NonNull MessageTopLevelComponent... components){
		getAction().addComponents(components);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper clearActionRows(){
		getAction().setComponents(List.of());
		return this;
	}
	
	@NonNull
	public SendMessageWrapper content(String content){
		getAction().setContent(content);
		return this;
	}
	
	@NonNull
	public SendMessageWrapper setActionRows(@NonNull ActionRow... actionRows){
		getAction().setComponents(actionRows);
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
}
