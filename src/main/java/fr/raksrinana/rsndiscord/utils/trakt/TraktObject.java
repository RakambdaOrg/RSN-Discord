package fr.raksrinana.rsndiscord.utils.trakt;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.util.Locale;

public interface TraktObject extends Comparable<TraktObject>{
	void fillEmbed(@NonNull Locale locale, @NonNull EmbedBuilder builder);
	
	URL getUrl();
}
