package fr.raksrinana.rsndiscord.modules.series.trakt.model;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;

public interface ITraktObject extends Comparable<ITraktObject>{
	void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	URL getUrl();
}
