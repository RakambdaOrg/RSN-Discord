package fr.raksrinana.rsndiscord.api.themoviedb.model;

import java.net.URL;
import java.util.Optional;

public interface MediaDetails{
	Optional<URL> getPosterURL(int seasonNumber);
	
	Optional<URL> getPosterURL();
}
