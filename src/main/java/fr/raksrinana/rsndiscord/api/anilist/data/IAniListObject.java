package fr.raksrinana.rsndiscord.api.anilist.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.net.URL;

public interface IAniListObject extends Comparable<IAniListObject>{
	void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder);
	
	int getId();
	
	@NotNull URL getUrl();
}
