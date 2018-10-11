package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.JSONFiller;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import java.awt.*;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public abstract class AniListListActivity implements JSONFiller{
	private static final String QUERY = "ListActivity{\n" + "userId\n" + "type\n" + "createdAt\n" + "progress\n" + "siteUrl\n" + AniListMedia.getQuery() + "}";
	
	private final AniListActivityType type;
	private Date createdAt;
	private URL url;
	private String progress;
	private AniListMedia media;
	
	protected AniListListActivity(final AniListActivityType type){
		this.type = type;
	}
	
	public static AniListListActivity buildFromJSON(final JSONObject json) throws Exception{
		final var change = AniListActivityType.valueOf(json.getString("type")).getInstance();
		change.fromJSON(json);
		return change;
	}
	
	public void fromJSON(final JSONObject json) throws Exception{
		this.createdAt = new Date(json.getInt("createdAt") * 1000L);
		this.url = new URL(json.getString("siteUrl"));
		this.progress = Utilities.getJSONMaybe(json, String.class, "progress");
		this.media = AniListMedia.buildFromJSON(json.getJSONObject("media"));
	}
	
	public void fillEmbed(final EmbedBuilder builder){
		builder.setColor(getColor());
		builder.setTimestamp(getCreatedAt().toInstant());
		
		if(Objects.isNull(getProgress())){
			builder.setDescription("Added to list");
		}
		else{
			builder.setDescription(StringUtils.capitalize(getMedia().getProgressType(getProgress().contains("-"))) + " " + getProgress());
		}
		
		getMedia().fillEmbed(builder);
	}
	
	protected abstract Color getColor();
	
	public Date getCreatedAt(){
		return createdAt;
	}
	
	public AniListMedia getMedia(){
		return media;
	}
	
	public String getProgress(){
		return progress;
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("type", type).append("createdAt", createdAt).append("url", url).append("progress", progress).append("media", media).toString();
	}
	
	public URL getUrl(){
		return url;
	}
	
	public AniListActivityType getType(){
		return type;
	}
}
