package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@Slf4j
public class InteractionEditMessageWrapper extends MessageWrapper<WebhookMessageEditAction<Message>>{
	private final ISnowflake target;
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		super(hook.editOriginalEmbeds(embed));
		this.target = target;
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		super(hook.editOriginal(message));
		this.target = target;
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageTopLevelComponent... layouts){
		super(hook.editOriginalComponents(layouts));
		this.target = target;
	}
	
	@NotNull
	public InteractionEditMessageWrapper clearActionRows(){
		getAction().setComponents(List.of());
		return this;
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Replied with edited message to interaction in {} : {}", target, value.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit interaction message {}", target, throwable);
	}
}
