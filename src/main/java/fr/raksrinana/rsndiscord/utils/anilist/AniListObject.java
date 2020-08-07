package fr.raksrinana.rsndiscord.utils.anilist;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;

public interface AniListObject extends Comparable<AniListObject>{
	void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	int getId();
	
	@NonNull URL getUrl();
}
