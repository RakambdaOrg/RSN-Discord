package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.amqp.message.DeleteMessageDelayMessage;
import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public abstract class MessageWrapper<T extends RestAction<Message>> extends ActionWrapper<Message, T>{
	public MessageWrapper(@NonNull T action){
		super(action);
	}
	
	@NonNull
	public CompletableFuture<Message> submitAndDelete(@NonNull Duration duration, @NonNull RabbitService rabbitService){
		return submit().thenApply(m -> {
			rabbitService.scheduleMessage(duration, new DeleteMessageDelayMessage(m.getChannel().getIdLong(), m.getIdLong()));
			return m;
		});
	}
	
	@NonNull
	public CompletableFuture<Message> submitAndDelete(long minutes, @NonNull RabbitService rabbitService){
		return submitAndDelete(Duration.ofMinutes(minutes), rabbitService);
	}
}
