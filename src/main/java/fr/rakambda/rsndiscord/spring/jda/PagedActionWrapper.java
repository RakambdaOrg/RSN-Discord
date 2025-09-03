package fr.rakambda.rsndiscord.spring.jda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction.PaginationOrder;
import net.dv8tion.jda.api.utils.Procedure;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class PagedActionWrapper<R, T extends PaginationAction<R, T>> extends ActionWrapper<List<R>, T>{
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PROTECTED)
	private T pagedAction;
	
	public PagedActionWrapper(T action){
		super(action);
		pagedAction = action;
	}
	
	@NonNull
	public PagedActionWrapper<R, T> order(@NonNull PaginationOrder order){
		getAction().order(order);
		return this;
	}
	
	@NonNull
	public PagedActionWrapper<R, T> skipTo(long id){
		getAction().skipTo(id);
		return this;
	}
	
	@NonNull
	public CompletableFuture<List<R>> takeAsync(int limit){
		return logAction(pagedAction.takeAsync(limit));
	}
	
	@NonNull
	public CompletableFuture<?> foreachAsync(@NonNull Procedure<? super R> procedure){
		return pagedAction.forEachAsync(procedure);
	}
}
