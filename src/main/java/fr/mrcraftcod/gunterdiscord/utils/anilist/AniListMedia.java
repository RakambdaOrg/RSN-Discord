package fr.mrcraftcod.gunterdiscord.utils.anilist;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public class AniListMedia{
	private String title;
	private AniListSeason season;
	private AniListMediaType type;
	private AniListMediaFormat format;
	private AniListMediaStatus status;
	private Integer episodes;
	private Integer chapters;
	private URL url;
	private URL coverUrl;
	private boolean isAdult;
	
	private AniListMedia(){
	}
	
	public static AniListMedia fromJSON(final JSONObject json) throws MalformedURLException{
		final var media = new AniListMedia();
		
		media.title = json.getJSONObject("title").getString("userPreferred");
		media.season = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "season")).map(AniListSeason::valueOf).orElse(null);
		media.type = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "type")).map(AniListMediaType::valueOf).orElse(null);
		media.format = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "format")).map(AniListMediaFormat::valueOf).orElse(null);
		media.status = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "status")).map(AniListMediaStatus::valueOf).orElse(null);
		media.episodes = Utilities.getJSONMaybe(json, Integer.class, "episodes");
		media.chapters = Utilities.getJSONMaybe(json, Integer.class, "chapters");
		media.isAdult = json.getBoolean("isAdult");
		media.coverUrl = new URL(json.getJSONObject("coverImage").getString("large"));
		media.url = new URL(json.getString("siteUrl"));
		
		return media;
	}
	
	public String getProgressType(final boolean contains){
		return getType().getProgressType(contains);
	}
	
	public AniListMediaType getType(){
		return type;
	}
	
	public Integer getChapters(){
		return chapters;
	}
	
	public URL getCoverUrl(){
		return coverUrl;
	}
	
	public Integer getEpisodes(){
		return episodes;
	}
	
	public AniListMediaFormat getFormat(){
		return format;
	}
	
	public AniListSeason getSeason(){
		return season;
	}
	
	public AniListMediaStatus getStatus(){
		return status;
	}
	
	public String getTitle(){
		return title;
	}
	
	public URL getUrl(){
		return url;
	}
	
	public boolean isAdult(){
		return isAdult;
	}
}
