package fr.raksrinana.rsndiscord.utils.jda;

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
	public CompletableFuture<R> submit(){
		return getAction().submit();
	}
	
	@NotNull
	public CompletableFuture<R> delay(int seconds){
		return delay(Duration.ofSeconds(seconds));
	}
	
	@NotNull
	public CompletableFuture<R> delay(@NotNull Duration duration){
		return getAction().delay(duration).submit()
				.thenApply(value -> {
					logSuccess(value);
					return value;
				})
				.exceptionally(throwable -> {
					logException(throwable);
					if(throwable instanceof CompletionException ce){
						throw ce;
					}
					throw new CompletionException(throwable);
				});
	}
	
	protected void logSuccess(R value){}
	
	protected void logException(Throwable throwable){}
}
