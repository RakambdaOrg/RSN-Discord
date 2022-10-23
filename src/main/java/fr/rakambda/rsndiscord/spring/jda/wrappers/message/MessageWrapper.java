package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.amqp.message.DeleteMessageDelayMessage;
import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public abstract class MessageWrapper<T extends RestAction<Message>> extends ActionWrapper<Message, T>{
	public MessageWrapper(@NotNull T action){
		super(action);
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(@NotNull Duration duration, @NotNull RabbitService rabbitService){
		return submit().thenApply(m -> {
			rabbitService.scheduleMessage(duration, new DeleteMessageDelayMessage(m.getChannel().getIdLong(), m.getIdLong()));
			return m;
		});
	}
	
	@NotNull
	public CompletableFuture<Message> submitAndDelete(long minutes, @NotNull RabbitService rabbitService){
		return submitAndDelete(Duration.ofMinutes(minutes), rabbitService);
	}
}
