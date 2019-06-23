package fr.mrcraftcod.gunterdiscord.utils.anilist;

import net.dv8tion.jda.api.EmbedBuilder;
import javax.annotation.Nonnull;
import java.net.URL;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListObject extends Comparable<AniListObject>{
	void fillEmbed(@Nonnull EmbedBuilder builder);
	
	int getId();
	
	@Nonnull
	URL getUrl();
}
