package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.MEDIA_DELETION;
import static java.awt.Color.RED;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MEDIA_MERGE")
@Getter
public class MediaDeletionNotification extends Notification{
	public static final String QUERY = """
			MediaDeletionNotification {
			    id
			    type
			    deletedMediaTitle
			    context
			    reason
			    createdAt
			}
			""";
	
	@JsonProperty("deletedMediaTitle")
	private String deletedMediaTitle;
	@JsonProperty("context")
	private String context;
	@JsonProperty("reason")
	private String reason;
	
	public MediaDeletionNotification(){
		super(MEDIA_DELETION);
	}
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.setTimestamp(getDate())
				.setColor(RED)
				.setTitle("Media entry deleted")
				.addField("Deleted titles", getDeletedMediaTitle(), true)
				.addField("Context", getContext(), true)
				.addField("Reason", getReason(), true);
	}
	
	@Override
	@NotNull
	public URL getUrl(){
		return FALLBACK_URL;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		MediaDeletionNotification that = (MediaDeletionNotification) o;
		return Objects.equals(deletedMediaTitle, that.deletedMediaTitle) && Objects.equals(context, that.context) && Objects.equals(reason, that.reason);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(deletedMediaTitle, context, reason);
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
