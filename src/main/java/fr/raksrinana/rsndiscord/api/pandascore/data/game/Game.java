package fr.raksrinana.rsndiscord.api.pandascore.data.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.Opponent;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.WrappedOpponent;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Game implements Comparable<Game>{
	@JsonProperty("begin_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startDate;
	@JsonProperty("complete")
	private boolean complete;
	@JsonProperty("detailed_stats")
	private boolean detailedStats;
	@JsonProperty("end_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endDate;
	@JsonProperty("finished")
	private boolean finished;
	@JsonProperty("forfeit")
	private boolean forfeit;
	@JsonProperty("id")
	private int id;
	@JsonProperty("length")
	@Nullable
	private Integer length;
	@JsonProperty("match_id")
	private int matchId;
	@JsonProperty("position")
	private int position;
	@JsonProperty("status")
	private GameStatus status;
	@JsonProperty("video_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL videoUrl;
	@JsonProperty("winner")
	private GameWinner winner;
	@JsonProperty("winner_type")
	@Nullable
	private OpponentType winnerType;
	
	public void fillEmbed(EmbedBuilder builder, Collection<WrappedOpponent<?>> opponents){
		var name = "Game %d".formatted(getPosition());
		var content = switch(getStatus()){
			case NOT_STARTED -> "⏳ Not started";
			case NOT_PLAYED -> "❌ Not played";
			case RUNNING -> "▶ In progress";
			case FINISHED -> {
				var duration = getDuration().map(Utilities::durationToString).orElse("Unknown length");
				var winner = getWinner(opponents)
						.map(Opponent::getShortName)
						.orElse("Unknown winner");
				yield "✅ Completed (%s), winner : %s".formatted(duration, winner);
			}
		};
		
		builder.addField(name, content, false);
	}
	
	@Override
	public int compareTo(@NotNull Game o){
		return Integer.compare(getPosition(), o.getPosition());
	}
	
	private Optional<Opponent> getWinner(Collection<WrappedOpponent<?>> opponents){
		return Optional.ofNullable(getWinner())
				.map(GameWinner::getId)
				.flatMap(id -> opponents.stream()
						.filter(o -> Objects.equals(o.getOpponent().getId(), id))
						.findFirst()
						.map(WrappedOpponent::getOpponent));
	}
	
	public Optional<Duration> getDuration(){
		return Optional.ofNullable(getLength())
				.map(Duration::ofSeconds);
	}
}
