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
import java.awt.Color;
import java.net.URL;
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
@JsonTypeName("RELATED_MEDIA_ADDITION")
public class RelatedMediaNotification extends Notification{
	private static final String QUERY = "RelatedMediaAdditionNotification {\n" + "id\n" + "type\n" + "createdAt\n" + Media.getQuery() + "\n}";
	@JsonProperty("media")
	private Media media;
	
	public RelatedMediaNotification(){
		super(NotificationType.RELATED_MEDIA_ADDITION);
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setTimestamp(this.getDate());
		builder.setColor(Color.PINK);
		builder.setTitle("New related media", this.getMedia().getUrl().toString());
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		this.getMedia().fillEmbed(builder);
	}
	
	@Nonnull
	private Media getMedia(){
		return this.media;
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return Optional.of(this.getMedia()).map(Media::getUrl).orElse(AniListUtils.FALLBACK_URL);
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
}
