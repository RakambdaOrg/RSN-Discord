package fr.raksrinana.rsndiscord.api.trakt.requests;

import org.jetbrains.annotations.NotNull;
import java.util.Set;

public interface ITraktPagedGetRequest<T> extends ITraktGetRequest<Set<T>>{
	@NotNull
	ITraktPagedGetRequest<T> getForPage(int page);
	
	default int getLimit(){
		return 50;
	}
	
	int getPage();
}
