package fr.raksrinana.rsndiscord.utils.trakt;

import net.dv8tion.jda.api.EmbedBuilder;

public interface TraktObject extends Comparable<TraktObject>{
	void fillEmbed(EmbedBuilder builder);
}
