package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditMessageWrapper{
	private final ISnowflake target;
	private final Message message;
	private MessageAction action;
	
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
	
	public EditMessageWrapper(@Nullable ISnowflake target, @NotNull Message message){
		this.target = target;
		this.message = message;
		this.action = message.editMessage(message);
	}
	
	@NotNull
	public EditMessageWrapper setActionRows(@NotNull ActionRow... actionRows){
		action = action.setActionRows(actionRows);
		return this;
	}
	
	@NotNull
	public EditMessageWrapper addActionRow(@NotNull Collection<Component> components){
		action = action.setActionRow(components);
		return this;
	}
	
	@NotNull
	public EditMessageWrapper addActionRow(@NotNull Component... components){
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
