package fr.raksrinana.rsndiscord.api.trakt.model;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

public interface ITraktObject extends Comparable<ITraktObject>{
	void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder);
	
	@Nullable
	URL getUrl();
}
