package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.schedule.ScheduleService.deleteMessageMins;

@Log4j2
public class InteractionEditMessageWrapper extends ActionWrapper<Message, WebhookMessageEditAction<Message>>{
	private final ISnowflake target;
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		super(hook.editOriginalEmbeds(embed));
		this.target = target;
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		super(hook.editOriginal(message));
		this.target = target;
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull LayoutComponent... layouts){
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
		log.info("Replied with edited message to slash command in {} : {}", target, value.getContentRaw());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit slash command message {}", target, throwable);
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(int minutes){
		return submit().thenApply(deleteMessageMins(minutes));
	}
}
