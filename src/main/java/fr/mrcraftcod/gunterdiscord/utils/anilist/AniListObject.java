package fr.mrcraftcod.gunterdiscord.utils.anilist;

import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListObject{
	void fromJSON(JSONObject object) throws Exception;
	
	void fillEmbed(EmbedBuilder builder);
	
	int getId();
	
	String getUrl();
}
