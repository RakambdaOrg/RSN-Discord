package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.AIRING;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("AIRING")
@Getter
public class AiringNotification extends INotification{
	@Getter
	private static final String QUERY = """
			AiringNotification {
			    id
			    type
			    episode
			    createdAt
			    %s
			}
			""".formatted(IMedia.getQUERY());
	@JsonProperty("episode")
	private int episode;
	@JsonProperty("media")
	private IMedia media;
	
	public AiringNotification(){
		super(AIRING);
	}
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull final EmbedBuilder builder){
		builder.setTimestamp(getDate())
				.setColor(GREEN)
				.setTitle(translate(guild, "anilist.release"), getMedia().getUrl().toString())
				.addField(translate(guild, "anilist.episode"), String.valueOf(getEpisode()), true)
				.addBlankField(false)
				.addField(translate(guild, "anilist.media"), "", false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@Override
	@NonNull
	public URL getUrl(){
		return Optional.of(getMedia()).map(IMedia::getUrl).orElse(FALLBACK_URL);
	}
	
	@Override
	public int hashCode(){
		return this.getEpisode();
	}
	
	@Override
	public boolean equals(final Object obj){
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
}
