package fr.raksrinana.rsndiscord.utils.trakt;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;

public interface TraktObject extends Comparable<TraktObject>{
	void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	URL getUrl();
}
