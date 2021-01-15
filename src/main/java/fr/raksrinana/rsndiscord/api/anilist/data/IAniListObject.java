package fr.raksrinana.rsndiscord.api.anilist.data;

import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;

public interface IAniListObject extends Comparable<IAniListObject>{
	void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	int getId();
	
	@NonNull URL getUrl();
}
