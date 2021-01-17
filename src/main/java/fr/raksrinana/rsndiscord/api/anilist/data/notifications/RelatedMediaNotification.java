package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.RELATED_MEDIA_ADDITION;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.PINK;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("RELATED_MEDIA_ADDITION")
@Getter
public class RelatedMediaNotification extends INotification{
	public static final String QUERY = """
			RelatedMediaAdditionNotification {
			    id
			    type
			    createdAt
			    %s
			}
			""".formatted(IMedia.QUERY);
	
	@JsonProperty("media")
	private IMedia media;
	
	public RelatedMediaNotification(){
		super(RELATED_MEDIA_ADDITION);
	}
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.setTimestamp(getDate())
				.setColor(PINK)
				.setTitle(translate(guild, "anilist.related"), getMedia().getUrl().toString())
				.addBlankField(false)
				.addField(translate(guild, "anilist.media"), "", false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@Override
	@NotNull
	public URL getUrl(){
		return Optional.of(getMedia())
				.map(IMedia::getUrl)
				.orElse(FALLBACK_URL);
	}
	
	@Override
	public int hashCode(){
		return getId();
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
