package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ComponentLayout;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.schedule.ScheduleService.deleteMessageMins;

@Log4j2
public class InteractionEditMessageWrapper{
	private final ISnowflake target;
	private WebhookMessageUpdateAction<Message> action;
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		this.target = target;
		action = hook.editOriginalEmbeds(embed);
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		this.target = target;
		action = hook.editOriginal(message);
	}
	
	public InteractionEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull ComponentLayout... layouts){
		this.target = target;
		action = hook.editOriginalComponents(layouts);
	}
	
	@NotNull
	public InteractionEditMessageWrapper clearActionRows(){
		action = action.setActionRows(List.of());
		return this;
	}
	
	@NotNull
	public InteractionEditMessageWrapper addActionRow(Component... components){
		action = action.setActionRow(components);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Message> submit(){
		return action.submit().thenApply(message -> {
			log.info("Replied with edited message to slash command in {} : {}", target, message.getContentRaw());
			return message;
		});
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(int minutes){
		return submit().thenApply(deleteMessageMins(minutes));
	}
}
