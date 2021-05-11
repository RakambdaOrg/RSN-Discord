package fr.raksrinana.rsndiscord.api.pandascore.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class R6Match {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("begin_at")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime startDate;
    @JsonProperty("end_at")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime endDate;
    @JsonProperty("modify_at")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime modifyDate;
    @JsonProperty("scheduled_at")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime scheduledAt;
    @JsonProperty("original_scheduled_at")
    @JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
    private ZonedDateTime originalScheduledAt;
    @JsonProperty("rescheduled")
    private boolean rescheduled;
    @JsonProperty("detailed_stats")
    private boolean detailedStats;
    @JsonProperty("draw")
    private boolean draw;
    @JsonProperty("forfeit")
    private boolean forfeit;
    @JsonProperty("game_advantage")
    private Integer gameAdvantage;
    @JsonProperty("number_of_games")
    private int numberOfGames;
    @JsonProperty("games")
    private List<R6Game> games;
    @JsonProperty("league_id")
    private Integer leagueId;
    @JsonProperty("league")
    private League league;
    @JsonProperty("live")
    private Live live;
    @JsonProperty("match_type")
    private MatchType matchType;
    @JsonProperty("opponents")
    private List<Opponent> opponents;
    @JsonProperty("results")
    private List<Result> results;
    @JsonProperty("serie_id")
    private Integer serieId;
    @JsonProperty("serie")
    private Serie serie;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("streams_list")
    private Streams streams;
    @JsonProperty("tournament_id")
    private Integer tournamentId;
    @JsonProperty("tournament")
    private Tournament tournament;
    @JsonProperty("videogame")
    private VideoGame videogame;
    @JsonProperty("videogame_version")
    private String videogameVersion;
    @JsonProperty("winner_id")
    private Integer winner_id;
    @JsonProperty("winner")
    private Winner winner;

}
