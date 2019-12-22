package fr.raksrinana.rsndiscord.utils.trakt;

import lombok.NonNull;
import java.util.Map;

public interface TraktGetRequest<T> extends TraktRequest<T>{
	@NonNull Map<String, String> getParameters();
}
