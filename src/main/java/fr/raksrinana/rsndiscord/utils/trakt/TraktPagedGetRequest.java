package fr.raksrinana.rsndiscord.utils.trakt;

import java.util.Set;

public interface TraktPagedGetRequest<T> extends TraktGetRequest<Set<T>>{
	TraktPagedGetRequest<T> getForPage(int page);
	
	default int getLimit(){
		return 50;
	}
	
	int getPage();
}
