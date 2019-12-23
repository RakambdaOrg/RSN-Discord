package fr.raksrinana.rsndiscord.utils.trakt;

import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;

public interface TraktObject extends Comparable<TraktObject>{
	void fillEmbed(EmbedBuilder builder);
	
	URL getUrl();
}
