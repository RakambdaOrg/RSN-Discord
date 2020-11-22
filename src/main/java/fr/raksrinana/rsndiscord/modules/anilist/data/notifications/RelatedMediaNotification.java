package fr.raksrinana.rsndiscord.modules.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.modules.anilist.data.media.IMedia;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.anilist.AniListUtils.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.modules.anilist.data.notifications.NotificationType.RELATED_MEDIA_ADDITION;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.PINK;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("RELATED_MEDIA_ADDITION")
@Getter
public class RelatedMediaNotification extends INotification{
	@Getter
	private static final String QUERY = """
			RelatedMediaAdditionNotification {
			    id
			    type
			    createdAt
			    %s
			}
			""".formatted(IMedia.getQUERY());
	@JsonProperty("media")
	private IMedia media;
	
	public RelatedMediaNotification(){
		super(RELATED_MEDIA_ADDITION);
	}
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull final EmbedBuilder builder){
		builder.setTimestamp(getDate())
				.setColor(PINK)
				.setTitle(translate(guild, "anilist.related"), getMedia().getUrl().toString())
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
		return this.getId();
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
