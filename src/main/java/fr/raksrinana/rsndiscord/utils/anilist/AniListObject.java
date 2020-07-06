package fr.raksrinana.rsndiscord.utils.anilist;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.util.Locale;

public interface AniListObject extends Comparable<AniListObject>{
	void fillEmbed(@NonNull Locale locale, @NonNull EmbedBuilder builder);
	
	int getId();
	
	@NonNull URL getUrl();
}
