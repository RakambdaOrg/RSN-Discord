package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditMessageWrapper{
	private final Message message;
	private MessageAction action;
	
	public EditMessageWrapper(@NotNull Message message, @NotNull String content){
		this.message = message;
		action = message.editMessage(content);
	}
	
	public EditMessageWrapper(@NotNull Message message, @NotNull MessageEmbed embed){
		this.message = message;
		action = message.editMessageEmbeds(embed);
	}
	
	public EditMessageWrapper(@NotNull Message message){
		this.message = message;
		action = message.editMessage(message);
	}
	
	@NotNull
	public EditMessageWrapper setActionRow(@NotNull Collection<Component> components){
		action = action.setActionRow(components);
		return this;
	}
	
	@NotNull
	public EditMessageWrapper setActionRow(@NotNull Component... components){
		action = action.setActionRow(components);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Message> submit(){
		return action.submit()
				.thenApply(m -> {
					log.info("Edited message {} with content: {}", message, message.getContentRaw());
					
					return m;
				});
	}
}
