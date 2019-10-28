package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("AIRING")
public class AiringNotification extends Notification{
	private static final String QUERY = "AiringNotification {\n" + "id\n" + "type\n" + "episode\n" + "createdAt\n" + Media.getQuery() + "\n}";
	@JsonProperty("episode")
	private int episode;
	@JsonProperty("media")
	private Media media;
	
	public AiringNotification(){
		super(NotificationType.AIRING);
	}
	
	@Override
	public boolean equals(@Nullable final Object obj){
		if(!(obj instanceof AiringNotification)){
			return false;
		}
		final var notification = (AiringNotification) obj;
		return Objects.equals(notification.getEpisode(), this.getEpisode()) && Objects.equals(notification.getMedia(), this.getMedia());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Nonnull
	private Media getMedia(){
		return this.media;
	}
	
	private int getEpisode(){
		return this.episode;
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setTimestamp(this.getDate());
		builder.setColor(Color.GREEN);
		builder.setTitle("New release", this.getMedia().getUrl().toString());
		builder.addField("Episode", String.valueOf(this.getEpisode()), true);
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		this.getMedia().fillEmbed(builder);
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return Optional.of(this.getMedia()).map(Media::getUrl).orElse(AniListUtils.FALLBACK_URL);
	}
	
	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
	
	@Override
	public int hashCode(){
		return this.getEpisode();
	}
}
