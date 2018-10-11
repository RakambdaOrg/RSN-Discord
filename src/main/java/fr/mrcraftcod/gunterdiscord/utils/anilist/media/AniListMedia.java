package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.JSONFiller;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import java.net.URL;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public abstract class AniListMedia implements JSONFiller{
	private static final String QUERY = "media {\n" + "id\n" + "title {\n" + "userPreferred\n" + "}\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "isAdult\n" + "coverImage{\n" + "large\n" + "}\n" + "siteUrl" + "}";
	
	private String title;
	private final AniListMediaType type;
	private AniListMediaSeason season;
	private AniListMediaFormat format;
	private AniListMediaStatus status;
	private URL url;
	private URL coverUrl;
	private boolean isAdult;
	
	protected AniListMedia(final AniListMediaType type){
		this.type = type;
	}
	
	public static AniListMedia buildFromJSON(final JSONObject json) throws Exception{
		final var media = AniListMediaType.valueOf(json.getString("type")).getInstance();
		media.fromJSON(json);
		return media;
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		this.title = json.getJSONObject("title").getString("userPreferred");
		this.season = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "season")).map(AniListMediaSeason::valueOf).orElse(null);
		this.format = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "format")).map(AniListMediaFormat::valueOf).orElse(null);
		this.status = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "status")).map(AniListMediaStatus::valueOf).orElse(null);
		this.url = new URL(json.getString("siteUrl"));
		this.isAdult = json.getBoolean("isAdult");
		this.coverUrl = new URL(json.getJSONObject("coverImage").getString("large"));
	}
	
	public void fillEmbed(final EmbedBuilder builder){
		builder.setTitle(getTitle());
		builder.addField("Format", Optional.ofNullable(getFormat()).map(Enum::name).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.ofNullable(getStatus()).map(Enum::name).orElse("UNKNOWN"), true);
		if(isAdult()){
			builder.addField("Adult content", "true", true);
		}
		builder.addField("Link", getUrl().toString(), false);
		builder.setThumbnail(getCoverUrl().toString());
	}
	
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("title", title).append("season", season).append("type", type).append("format", format).append("status", status).append("url", url).append("coverUrl", coverUrl).append("isAdult", isAdult).toString();
	}
	
	public AniListMediaType getType(){
		return type;
	}
	
	public URL getCoverUrl(){
		return coverUrl;
	}
	
	public AniListMediaFormat getFormat(){
		return format;
	}
	
	public static String getQuery(){
		return QUERY;
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
	
	public AniListMediaSeason getSeason(){
		return season;
	}
}
