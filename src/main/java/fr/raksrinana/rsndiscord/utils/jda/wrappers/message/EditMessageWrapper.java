package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

@Log4j2
public class EditMessageWrapper extends ActionWrapper<Message, MessageAction>{
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
		super(message.editMessage(message));
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
