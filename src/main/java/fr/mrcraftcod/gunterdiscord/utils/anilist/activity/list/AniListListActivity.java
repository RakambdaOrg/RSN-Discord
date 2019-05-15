package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.JSONFiller;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.awt.Color;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@SuppressWarnings("ALL")
public abstract class AniListListActivity implements JSONFiller, AniListDatedObject{
	private static final String QUERY = "ListActivity{\n" + "id\n" + "userId\n" + "type\n" + "createdAt\n" + "progress\n" + "siteUrl\n" + AniListMedia.getQuery() + "}";
	
	private final AniListActivityType type;
	private Date createdAt;
	private String url;
	private String progress;
	private AniListMedia media;
	private int id;
	
	protected AniListListActivity(final AniListActivityType type){
		this.type = type;
	}
	
	public static AniListListActivity buildFromJSON(final JSONObject json) throws Exception{
		final var change = AniListActivityType.valueOf(json.getString("type")).getInstance();
		change.fromJSON(json);
		return change;
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		this.id = json.getInt("id");
		this.createdAt = new Date(json.optInt("createdAt") * 1000L);
		this.url = json.getString("siteUrl");
		this.progress = Utilities.getJSONMaybe(json, String.class, "progress");
		this.media = AniListMedia.buildFromJSON(json.getJSONObject("media"));
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof AniListListActivity && Objects.equals(((AniListListActivity) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	protected abstract Color getColor();
	
	@Override
	public int getId(){
		return this.id;
	}
	
	public AniListMedia getMedia(){
		return media;
	}
	
	public String getProgress(){
		return progress;
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setColor(getColor());
		builder.setTimestamp(getDate().toInstant());
		
		if(Objects.isNull(getProgress())){
			builder.setDescription("Added to list");
		}
		else{
			builder.setDescription(StringUtils.capitalize(getMedia().getProgressType(getProgress().contains("-"))) + " " + getProgress());
		}
		
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	public Date getDate(){
		return createdAt;
	}
	
	public AniListActivityType getType(){
		return type;
	}
	
	@Override
	public String getUrl(){
		return url;
	}
	
	public static String getQuery(){
		return QUERY;
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public int compareTo(@NotNull final AniListObject o){
		if(o instanceof AniListDatedObject){
			return getDate().compareTo(((AniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
}