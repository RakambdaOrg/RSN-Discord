package fr.raksrinana.rsndiscord.utils.jda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import org.jetbrains.annotations.NotNull;
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
	
	@NotNull
	public PagedActionWrapper<R, T> order(@NotNull PaginationAction.PaginationOrder order){
		getAction().order(order);
		return this;
	}
	
	@NotNull
	public CompletableFuture<List<R>> takeAsync(int limit){
		return logAction(pagedAction.takeAsync(limit));
	}
}
