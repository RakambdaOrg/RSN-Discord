package fr.rakambda.rsndiscord.spring.jda;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@AllArgsConstructor
public abstract class ActionWrapper<R, T extends RestAction<R>>{
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private T action;
	
	@NotNull
	protected CompletableFuture<R> logAction(@NotNull CompletableFuture<R> future){
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
	
	@NotNull
	public CompletableFuture<R> submit(){
		return logAction(getAction().submit());
	}
	
	@NotNull
	public CompletableFuture<R> delay(@NotNull Duration duration){
		return logAction(getAction().delay(duration).submit());
	}
	
	@NotNull
	public CompletableFuture<R> delay(int seconds){
		return delay(Duration.ofSeconds(seconds));
	}
	
	protected void logSuccess(R value){}
	
	protected void logException(Throwable throwable){}
}
