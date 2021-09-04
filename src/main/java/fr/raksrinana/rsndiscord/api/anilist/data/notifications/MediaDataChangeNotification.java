package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.anilist.data.media.Media;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.MEDIA_DATA_CHANGE;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.ORANGE;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MEDIA_DATA_CHANGE")
@Getter
public class MediaDataChangeNotification extends Notification{
	public static final String QUERY = """
			MediaDataChangeNotification {
			    id
			    type
			    context
			    reason
			    createdAt
			    %s
			}
			""".formatted(Media.QUERY);
	
	@JsonProperty("context")
	private String context;
	@JsonProperty("reason")
	private String reason;
	@JsonProperty("media")
	private Media media;
	
	public MediaDataChangeNotification(){
		super(MEDIA_DATA_CHANGE);
	}
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.setTimestamp(getDate())
				.setColor(ORANGE)
				.setTitle("Media data changed", getMedia().getUrl().toString())
				.addField("Context", getContext(), true)
				.addField("Reason", getReason(), true)
				.addBlankField(false)
				.addField(translate(guild, "anilist.media"), "", false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@Override
	@NotNull
	public URL getUrl(){
		return Optional.of(getMedia())
				.map(Media::getUrl)
				.orElse(FALLBACK_URL);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		MediaDataChangeNotification that = (MediaDataChangeNotification) o;
		return Objects.equals(context, that.context) && Objects.equals(reason, that.reason) && Objects.equals(media, that.media);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(context, reason, media);
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}