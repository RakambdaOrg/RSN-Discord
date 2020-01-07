package fr.raksrinana.rsndiscord.utils.themoviedb;

import java.util.Map;

public interface TMDBGetRequest<T> extends TMDBRequest<T>{
	Map<String, String> getParameters();
}
