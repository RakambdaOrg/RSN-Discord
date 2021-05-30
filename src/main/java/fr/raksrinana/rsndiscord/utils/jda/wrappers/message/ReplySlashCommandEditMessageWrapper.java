package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessageMins;

public class ReplySlashCommandEditMessageWrapper{
	private final ISnowflake target;
	private WebhookMessageUpdateAction<Message> action;
	
	public ReplySlashCommandEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull MessageEmbed embed){
		this.target = target;
		this.action = hook.editOriginalEmbeds(embed);
	}
	
	public ReplySlashCommandEditMessageWrapper(@Nullable ISnowflake target, @NotNull InteractionHook hook, @NotNull String message){
		this.target = target;
		this.action = hook.editOriginal(message);
	}
	
	public ReplySlashCommandEditMessageWrapper setActionRow(Button... button){
		action = action.setActionRow(button);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Message> submit(){
		return action.submit()
				.thenApply(message -> {
					Logger logger;
					if(target instanceof Guild g){
						logger = Log.getLogger(g);
					}
					else{
						logger = Log.getLogger();
					}
					logger.info("Replied with edited message to slash command in {} : {}", target, message.getContentRaw());
					return message;
				});
	}
	
	@NotNull
	public CompletableFuture<Void> submitAndDelete(int minutes){
		return submit().thenAccept(deleteMessageMins(minutes));
	}
}
