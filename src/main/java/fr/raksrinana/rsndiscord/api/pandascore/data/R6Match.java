package fr.raksrinana.rsndiscord.api.pandascore.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.pandascore.data.game.Game;
import fr.raksrinana.rsndiscord.api.pandascore.data.league.League;
import fr.raksrinana.rsndiscord.api.pandascore.data.match.*;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.Opponent;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.WrappedOpponent;
import fr.raksrinana.rsndiscord.api.pandascore.data.serie.Serie;
import fr.raksrinana.rsndiscord.api.pandascore.data.tournament.Tournament;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class R6Match{
	@JsonProperty("begin_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startDate;
	@JsonProperty("detailed_stats")
	private boolean detailedStats;
	@JsonProperty("draw")
	private boolean draw;
	@JsonProperty("end_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endDate;
	@JsonProperty("forfeit")
	private boolean forfeit;
	@JsonProperty("game_advantage")
	@Nullable
	private Integer gameAdvantageOpponentId;
	@JsonProperty("games")
	@NotNull
	private List<Game> games;
	@JsonProperty("id")
	private int id;
	@JsonProperty("league")
	@NotNull
	private League league;
	@JsonProperty("league_id")
	private int leagueId;
	@JsonProperty("live")
	@NotNull
	private MatchLive live;
	@JsonProperty("match_type")
	@NotNull
	private MatchType matchType;
	@JsonProperty("modified_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime modifyDate;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("number_of_games")
	private int numberOfGames;
	@JsonProperty("opponents")
	@NotNull
	private List<WrappedOpponent<?>> opponents;
	@JsonProperty("original_scheduled_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime originalScheduledAt;
	@JsonProperty("rescheduled")
	private boolean rescheduled = false;
	@JsonProperty("results")
	private List<Result> results;
	@JsonProperty("scheduled_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime scheduledAt;
	@JsonProperty("serie")
	@NotNull
	private Serie serie;
	@JsonProperty("serie_id")
	private int serieId;
	@JsonProperty("slug")
	@Nullable
	private String slug;
	@JsonProperty("status")
	@NotNull
	private MatchStatus status;
	@JsonProperty("streams_list")
	@NotNull
	private List<Stream> streams;
	@JsonProperty("tournament")
	@NotNull
	private Tournament tournament;
	@JsonProperty("tournament_id")
	private int tournamentId;
	@JsonProperty("videogame")
	@NotNull
	private VideoGame videogame;
	@JsonProperty("videogame_version")
	@Nullable
	private VideoGameVersion videogameVersion;
	@JsonProperty("winner")
	@Nullable
	private Opponent winner;
	@JsonProperty("winner_id")
	@Nullable
	private Integer winner_id;
	
	public void fillEmbed(EmbedBuilder builder){
		var date = Optional.ofNullable(getStartDate())
				.filter(d -> d.isAfter(getModifyDate()))
				.orElse(getModifyDate());
		var url = getStreams().stream()
				.sorted().findFirst()
				.flatMap(Stream::getUrlAsString)
				.or(() -> getLeague().getUrlAsString())
				.orElse(null);
		var tier = Optional.ofNullable(getSerie().getTier()).map(" (%s)"::formatted).orElse("");
		var title = "%s%s".formatted(getLeague().getName(), tier);
		
		builder.setColor(getStatus().getColor())
				.setTimestamp(date)
				.setThumbnail(getLeague().getImageUrlAsString())
				.setTitle(title, url)
				.setDescription(getName())
				.addField("Match type", getMatchType().name(), true);
		
		getGames().forEach(game -> game.fillEmbed(builder));
	}
}
