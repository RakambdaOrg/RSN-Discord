package fr.raksrinana.rsndiscord.api.trakt.model.users.history;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.api.trakt.TraktMediaType;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktDatedObject;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.DATE_TIME_MINUTE_FORMATTER;
import static java.awt.Color.GREEN;

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
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		fillEmbed(guild, builder, null);
	}
	
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder, @Nullable MediaDetails mediaDetails){
		builder.setFooter(Long.toString(getId()))
				.setColor(GREEN)
				.setTimestamp(getWatchedAt())
				.addField(translate(guild, "trakt.watched"), watchedAt.format(DATE_TIME_MINUTE_FORMATTER), true);
	}
	
	@Override
	public int compareTo(@NotNull ITraktObject o){
		if(o instanceof ITraktDatedObject){
			return getDate().compareTo(((ITraktDatedObject) o).getDate());
		}
		return 0;
	}
	
	@Override
	@NotNull
	public ZonedDateTime getDate(){
		return getWatchedAt();
	}
	
	@Nullable
	public abstract MediaIds getIds();
}
