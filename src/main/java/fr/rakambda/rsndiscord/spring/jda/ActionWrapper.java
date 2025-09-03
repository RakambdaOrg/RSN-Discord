package fr.rakambda.rsndiscord.spring.jda;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@AllArgsConstructor
public abstract class ActionWrapper<R, T extends RestAction<R>>{
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private T action;
	
	@NonNull
	protected CompletableFuture<R> logAction(@NonNull CompletableFuture<R> future){
		return future
				.thenApply(value -> {
					logSuccess(value);
					return value;
				})
				.exceptionally(throwable -> {
					logException(throwable);
					if(throwable instanceof RuntimeException re){
						throw re;
					}
					throw new CompletionException(throwable);
				});
	}
	
	@NonNull
	public CompletableFuture<R> submit(){
		return logAction(getAction().submit());
	}
	
	@NonNull
	public CompletableFuture<R> delay(@NonNull Duration duration){
		return logAction(getAction().delay(duration).submit());
	}
	
	@NonNull
	public CompletableFuture<R> delay(int seconds){
		return delay(Duration.ofSeconds(seconds));
	}
	
	protected void logSuccess(R value){}
	
	protected void logException(Throwable throwable){}
}
