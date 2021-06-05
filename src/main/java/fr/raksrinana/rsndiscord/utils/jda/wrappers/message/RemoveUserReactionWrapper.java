package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class RemoveUserReactionWrapper{
	private final ISnowflake target;
	private final MessageReaction messageReaction;
	private final User user;
	private final RestAction<Void> action;
	
	public RemoveUserReactionWrapper(@Nullable ISnowflake target, @NotNull MessageReaction messageReaction, @NotNull User user){
		this.target = target;
		this.messageReaction = messageReaction;
		this.user = user;
		this.action = messageReaction.removeReaction(user);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> {
					log.info("Removed reaction {} of {} from message {}", messageReaction.getReactionEmote(), user, messageReaction.getMessageIdLong());
				});
	}
}
