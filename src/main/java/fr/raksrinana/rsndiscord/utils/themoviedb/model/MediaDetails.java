package fr.raksrinana.rsndiscord.utils.themoviedb.model;

import java.net.URL;
import java.util.Optional;

public interface MediaDetails{
	Optional<URL> getPosterURL(int seasonNumber);
	
	Optional<URL> getPosterURL();
}
