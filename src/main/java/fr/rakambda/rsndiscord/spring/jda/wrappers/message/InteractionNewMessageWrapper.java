package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;

@Slf4j
public class InteractionNewMessageWrapper extends MessageWrapper<WebhookMessageCreateAction<Message>>{
	private final ISnowflake target;
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		super(hook.sendMessageEmbeds(embed));
		this.target = target;
	}
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		super(hook.sendMessage(message));
		this.target = target;
	}
	
	@NotNull
	public InteractionNewMessageWrapper ephemeral(boolean state){
		getAction().setEphemeral(state);
		return this;
	}
	
	@NotNull
	public InteractionNewMessageWrapper addActionRow(@NotNull ItemComponent... components){
		getAction().addActionRow(components);
		return this;
	}
	
	@NotNull
	public InteractionNewMessageWrapper addActionRow(@NotNull Collection<? extends ItemComponent> components){
		getAction().addActionRow(components);
		return this;
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Replied with new message to slash command in {} : {}", target, value.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to reply new message to slash command {}", target, throwable);
	}
}
