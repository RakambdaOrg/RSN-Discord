package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

@Slf4j
public class EditMessageWrapper extends MessageWrapper<MessageEditAction>{
	private final Message message;
	
	public EditMessageWrapper(@NotNull Message message, @NotNull String content){
		super(message.editMessage(content));
		this.message = message;
	}
	
	public EditMessageWrapper(@NotNull Message message, @NotNull MessageEmbed embed){
		super(message.editMessageEmbeds(embed));
		this.message = message;
	}
	
	public EditMessageWrapper(@NotNull Message message){
		super(message.editMessage(MessageEditBuilder.fromMessage(message).build()));
		this.message = message;
	}
	
	@NotNull
	public EditMessageWrapper setActionRow(@NotNull Collection<ItemComponent> components){
		getAction().setActionRow(components);
		return this;
	}
	
	@NotNull
	public EditMessageWrapper setActionRow(@NotNull ItemComponent... components){
		getAction().setActionRow(components);
		return this;
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Edited message {} with content: {}", message, message.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit message {}", message, throwable);
	}
}
