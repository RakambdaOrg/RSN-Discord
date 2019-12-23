package fr.raksrinana.rsndiscord.utils.trakt;

import java.util.Map;

public interface TraktGetRequest<T> extends TraktRequest<T>{
	Map<String, String> getParameters();
}
