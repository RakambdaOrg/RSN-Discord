package fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.AniListNotificationType;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URL;
import java.time.LocalDateTime;
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
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime createdAt = LocalDateTime.MIN;
	@JsonProperty("media")
	private AniListMedia media;
	@JsonProperty("id")
	private int id;
	
	public AniListAiringNotification(){
		this.type = AniListNotificationType.AIRING;
	}
	
	@Override
	public boolean equals(@Nullable final Object obj){
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
	
	@Nonnull
	private AniListMedia getMedia(){
		return this.media;
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setTimestamp(getDate());
		builder.setColor(Color.GREEN);
		builder.setTitle("New release", getMedia().getUrl().toString());
		builder.addField("Episode", "" + getEpisode(), true);
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	public int getEpisode(){
		return this.episode;
	}
	
	@Override
	@Nonnull
	public LocalDateTime getDate(){
		return this.createdAt;
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return this.getMedia().getUrl();
	}
	
	@Override
	public int compareTo(@Nonnull final AniListObject o){
		if(o instanceof AniListDatedObject){
			return getDate().compareTo(((AniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
	
	@Override
	public int hashCode(){
		return this.getEpisode();
	}
	
	@Nonnull
	public AniListNotificationType getType(){
		return this.type;
	}
}
