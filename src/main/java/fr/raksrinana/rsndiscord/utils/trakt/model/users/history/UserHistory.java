package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.trakt.TraktDatedObject;
import fr.raksrinana.rsndiscord.utils.trakt.TraktMediaType;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = UserMovieHistory.class, name = "movie"),
		@JsonSubTypes.Type(value = UserSerieHistory.class, name = "episode")
})
public abstract class UserHistory implements TraktDatedObject{
	protected static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@JsonProperty("id")
	private long id;
	@JsonProperty("watched_at")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime watchedAt;
	@JsonProperty("action")
	private String action;
	@JsonProperty("type")
	private TraktMediaType type;
	
	@Override
	public void fillEmbed(EmbedBuilder builder){
		builder.setFooter(Long.toString(this.getId()));
		builder.setColor(Color.GREEN);
		builder.setTimestamp(this.getWatchedAt());
		builder.addField("Watched at", watchedAt.format(Utilities.DATE_TIME_MINUTE_FORMATTER), true);
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		if(o instanceof TraktDatedObject){
			return this.getDate().compareTo(((TraktDatedObject) o).getDate());
		}
		return 0;
	}
	
	@Override
	public @NonNull LocalDateTime getDate(){
		return this.getWatchedAt();
	}
}
