package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.amqp.QuartzService;
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
	public CompletableFuture<Message> submitAndDelete(@NonNull Duration duration, @NonNull QuartzService quartzService){
		return submit().thenApply(m -> {
			quartzService.scheduleMessage(duration, new DeleteMessageDelayMessage(m.getChannel().getIdLong(), m.getIdLong()));
			return m;
		});
	}
	
	@NonNull
	public CompletableFuture<Message> submitAndDelete(long minutes, @NonNull QuartzService quartzService){
		return submitAndDelete(Duration.ofMinutes(minutes), quartzService);
	}
}
