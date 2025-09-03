package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collection;

@Slf4j
public class InteractionNewMessageWrapper extends MessageWrapper<WebhookMessageCreateAction<Message>>{
	private final ISnowflake target;
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NonNull InteractionHook hook, @NonNull MessageEmbed embed){
		super(hook.sendMessageEmbeds(embed));
		this.target = target;
	}
	
	public InteractionNewMessageWrapper(@Nullable ISnowflake target, @NonNull InteractionHook hook, @NonNull String message){
		super(hook.sendMessage(message));
		this.target = target;
	}
	
	@NonNull
	public InteractionNewMessageWrapper ephemeral(boolean state){
		getAction().setEphemeral(state);
		return this;
	}
	
	@NonNull
	public InteractionNewMessageWrapper addActionRow(@NonNull MessageTopLevelComponent... components){
		getAction().addComponents(components);
		return this;
	}
	
	@NonNull
	public InteractionNewMessageWrapper addActionRow(@NonNull Collection<? extends MessageTopLevelComponent> components){
		getAction().addComponents(components);
		return this;
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Replied with new message to interaction in {} : {}", target, value.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to reply new message to interaction {}", target, throwable);
	}
}
