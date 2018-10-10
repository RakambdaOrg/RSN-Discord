package fr.mrcraftcod.gunterdiscord.utils.anilist;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import org.json.JSONObject;
import java.net.URL;
import java.util.Date;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public class AniListChange{
	private AniListListType type;
	private Date createdAt;
	private URL url;
	private String progress;
	private AniListMedia media;
	
	private AniListChange(){
	}
	
	public static AniListChange fromJSON(final JSONObject json) throws Exception{
		final var change = new AniListChange();
		change.type = AniListListType.valueOf(json.getString("type"));
		change.createdAt = new Date(json.getInt("createdAt") * 1000L);
		change.url = new URL(json.getString("siteUrl"));
		change.progress = Utilities.getJSONMaybe(json, String.class, "progress");
		change.media = AniListMedia.fromJSON(json.getJSONObject("media"));
		return change;
	}
	
	public Date getCreatedAt(){
		return createdAt;
	}
	
	public AniListMedia getMedia(){
		return media;
	}
	
	public String getProgress(){
		return progress;
	}
	
	public AniListListType getType(){
		return type;
	}
	
	public URL getUrl(){
		return url;
	}
}
