package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.JSONFiller;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public abstract class AniListMedia implements JSONFiller, AniListObject{
	private static final String QUERY = "media {\n" + "id\n" + "title {\n" + "userPreferred\n" + "}\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "isAdult\n" + "coverImage{\n" + "large\n" + "}\n" + "siteUrl" + "}";
	
	private String title;
	private final AniListMediaType type;
	private AniListMediaSeason season;
	private AniListMediaFormat format;
	private AniListMediaStatus status;
	private String url;
	private String coverUrl;
	private boolean isAdult;
	private int id;
	
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
		this.id = json.getInt("id");
		this.title = json.getJSONObject("title").getString("userPreferred");
		this.season = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "season")).map(AniListMediaSeason::valueOf).orElse(null);
		this.format = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "format")).map(AniListMediaFormat::valueOf).orElse(null);
		this.status = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "status")).map(AniListMediaStatus::valueOf).orElse(null);
		this.url = Optional.ofNullable(Utilities.getJSONMaybe(json, String.class, "siteUrl")).orElse(null);
		this.isAdult = Optional.ofNullable(Utilities.getJSONMaybe(json, Boolean.class, "isAdult")).orElse(false);
		this.coverUrl = json.getJSONObject("coverImage").getString("large");
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof AniListMedia && Objects.equals(((AniListMedia) obj).getId(), getId());
	}
	
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("id", id).append("title", title).append("season", season).append("type", type).append("format", format).append("status", status).append("url", url).append("coverUrl", coverUrl).append("isAdult", isAdult).toString();
	}
	
	public AniListMediaType getType(){
		return type;
	}
	
	@Override
	public int getId(){
		return this.id;
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
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setTitle(getTitle());
		builder.addField("Format", Optional.ofNullable(getFormat()).map(Enum::name).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.ofNullable(getStatus()).map(Enum::name).orElse("UNKNOWN"), true);
		if(isAdult()){
			builder.addField("Adult content", "true", true);
		}
		builder.addField("Link", getUrl(), false);
		builder.setThumbnail(getCoverUrl());
	}
	
	public boolean isAdult(){
		return isAdult;
	}
	
	public AniListMediaSeason getSeason(){
		return season;
	}
	
	public String getCoverUrl(){
		return coverUrl;
	}
	
	@Override
	public String getUrl(){
		return url;
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public int compareTo(@NotNull final AniListObject o){
		return Integer.compare(getId(), o.getId());
	}
}
