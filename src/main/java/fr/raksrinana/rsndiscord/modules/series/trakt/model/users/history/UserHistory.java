package fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.modules.series.trakt.TraktMediaType;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktDatedObject;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = UserMovieHistory.class, name = "movie"),
		@JsonSubTypes.Type(value = UserSerieHistory.class, name = "episode")
})
public abstract class UserHistory implements ITraktDatedObject{
	protected static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@JsonProperty("id")
	private long id;
	@JsonProperty("watched_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime watchedAt;
	@JsonProperty("action")
	private String action;
	@JsonProperty("type")
	private TraktMediaType type;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		fillEmbed(guild, builder, null);
	}
	
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder, MediaDetails mediaDetails){
		builder.setFooter(Long.toString(this.getId()));
		builder.setColor(Color.GREEN);
		builder.setTimestamp(this.getWatchedAt());
		builder.addField(translate(guild, "trakt.watched"), watchedAt.format(Utilities.DATE_TIME_MINUTE_FORMATTER), true);
	}
	
	@Override
	public int compareTo(@NonNull ITraktObject o){
		if(o instanceof ITraktDatedObject){
			return this.getDate().compareTo(((ITraktDatedObject) o).getDate());
		}
		return 0;
	}
	
	@Override
	public @NonNull ZonedDateTime getDate(){
		return this.getWatchedAt();
	}
	
	public abstract MediaIds getIds();
}
