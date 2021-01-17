package fr.raksrinana.rsndiscord.api.themoviedb.model;

import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.Optional;

public interface MediaDetails{
	@NotNull
	Optional<URL> getPosterURL(int seasonNumber);
	
	@NotNull
	Optional<URL> getPosterURL();
}
