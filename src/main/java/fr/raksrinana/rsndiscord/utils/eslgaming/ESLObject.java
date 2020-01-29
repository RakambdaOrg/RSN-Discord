package fr.raksrinana.rsndiscord.utils.eslgaming;

import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;

public interface ESLObject extends Comparable<ESLObject>{
	void fillEmbed(EmbedBuilder builder);
	
	URL getUrl();
}
