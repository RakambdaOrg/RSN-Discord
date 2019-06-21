package fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.AniListNotificationType;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.Color;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AniListAiringNotification implements AniListDatedObject{
	private static final String QUERY = "AiringNotification {\n" + "id\n" + "type\n" + "episode\n" + "createdAt\n" + AniListMedia.getQuery() + "}\n";
	@JsonProperty("type")
	private final AniListNotificationType type;
	@JsonProperty("episode")
	private int episode;
	private Date createdAt = new Date(0);
	@JsonProperty("media")
	private AniListMedia media;
	@JsonProperty("id")
	private int id;
	
	public AniListAiringNotification(){
		this.type = AniListNotificationType.AIRING;
	}
	
	@JsonCreator
	public void fromJSON(@JsonProperty("createdAt") final long createdAt){
		this.createdAt = new Date(createdAt * 1000L);
	}
	
	@Override
	public boolean equals(final Object obj){
		if(!(obj instanceof AniListAiringNotification)){
			return false;
		}
		final var notification = (AniListAiringNotification) obj;
		return Objects.equals(notification.getEpisode(), getEpisode()) && Objects.equals(notification.getMedia(), getMedia());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setTimestamp(getDate().toInstant());
		builder.setColor(Color.GREEN);
		builder.setTitle("New release", getMedia().getUrl().toString());
		builder.addField("Episode", "" + getEpisode(), true);
		
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	public Date getDate(){
		return this.createdAt;
	}
	
	public int getEpisode(){
		return this.episode;
	}
	
	private AniListMedia getMedia(){
		return this.media;
	}
	
	@Override
	public URL getUrl(){
		return null;
	}
	
	public static String getQuery(){
		return QUERY;
	}
	
	public AniListNotificationType getType(){
		return this.type;
	}
	
	@Override
	public int hashCode(){
		return this.getEpisode();
	}
	
	@Override
	public int compareTo( final AniListObject o){
		if(o instanceof AniListDatedObject){
			return getDate().compareTo(((AniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
}
