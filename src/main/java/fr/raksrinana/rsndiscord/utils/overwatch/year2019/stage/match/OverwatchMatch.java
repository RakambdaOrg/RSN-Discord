package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.json.OverwatchIDTeamDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.*;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.bracket.OverwatchBracket;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.game.OverwatchGame;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.tournament.OverwatchTournament;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchMatch implements Comparable<OverwatchMatch>{
	@JsonProperty("id")
	private int id;
	@JsonProperty("competitors")
	private final Set<OverwatchCompetitor> competitors = new HashSet<>();
	@JsonProperty("scores")
	private final List<OverwatchScore> scores = new ArrayList<>();
	@JsonProperty("conclusionValue")
	private final int conclusionValue = -1;
	@JsonProperty("conclusionStrategy")
	private OverwatchConclusionStrategy conclusionStrategy;
	@JsonProperty("winner")
	@JsonDeserialize(using = OverwatchIDTeamDeserializer.class)
	private int winner;
	@JsonProperty("home")
	private String home;
	@JsonProperty("state")
	private OverwatchState state;
	@JsonProperty("status")
	private OverwatchStatus status;
	@JsonProperty("statusReason")
	private OverwatchStatusReason statusReason;
	@JsonProperty("attributes")
	private OverwatchMatchAttributes attributes;
	@JsonProperty("attributesVersion")
	private String attributesVersion;
	@JsonProperty("games")
	private final Set<OverwatchGame> games = new HashSet<>();
	@JsonProperty("clientHints")
	private List<String> clientHints; //TODO
	@JsonProperty("bracket")
	private OverwatchBracket bracket;
	@JsonProperty("dateCreated")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime dateCreated;
	@JsonProperty("flags")
	private List<String> flags; //TODO
	@JsonProperty("handle")
	private String handle;
	@JsonProperty("competitorStatuses")
	private List<OverwatchCompetitorStatus> competitorStatuses;
	@JsonProperty("timeZone")
	private String timeZone;
	@JsonProperty("actualStartDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime actualStartDate;
	@JsonProperty("actualEndDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime actualEndDate;
	@JsonProperty("startDate")
	private String startDateString;
	@JsonProperty("endDate")
	private String endDateString;
	@JsonProperty("showStartTime")
	private boolean showStartTime;
	@JsonProperty("showEndTime")
	private boolean showEndTime;
	@JsonProperty("startDateTS")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime startDate;
	@JsonProperty("endDateTS")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime endDate;
	@JsonProperty("youtubeId")
	private String youtubeId;
	@JsonProperty("wins")
	private List<Integer> wins;
	@JsonProperty("ties")
	private List<Integer> ties;
	@JsonProperty("losses")
	private List<Integer> losses;
	@JsonProperty("videos")
	private Set<OverwatchVideo> videos;
	@JsonProperty("tournament")
	private OverwatchTournament tournament;
	@JsonProperty("broadcastChannels")
	private Set<OverwatchBroadcastChannel> broadcastChannels;
	@JsonProperty("round")
	private int round;
	@JsonProperty("ordinal")
	private int ordinal;
	@JsonProperty("bestOf")
	private final int bestOf = -1;
	
	public boolean hasEnded(){
		return this.state == OverwatchState.CONCLUDED;
	}
	
	@Override
	public int compareTo(@NonNull final OverwatchMatch overwatchMatch){
		return this.getStartDateString().compareTo(overwatchMatch.getStartDateString());
	}
	
	public boolean inProgress(){
		return this.state == OverwatchState.IN_PROGRESS;
	}
	
	public EmbedBuilder buildEmbed(final User user){
		final var builder = Utilities.buildEmbed(user, Color.GREEN, this.getVsCompetitorsNames(), (Objects.nonNull(this.getYoutubeId()) && !this.getYoutubeId().isBlank()) ? ("https://youtube.com/watch?v=" + this.getYoutubeId()) : null);
		builder.setTimestamp(this.getStartDate());
		builder.setDescription("Score: " + this.getScores().stream().map(OverwatchScore::getValue).map(Object::toString).collect(Collectors.joining(" - ")));
		builder.addField("State", this.getState().asString(), true);
		builder.addField("Conclusion strategy", this.getConclusionStrategy().asString(this), true);
		this.getGames().forEach(game -> builder.addField("Game " + game.getNumber(), game.getAttributes().getMapName().orElse(null) + ": " + game.getPoints().stream().map(Object::toString).collect(Collectors.joining(" - ")) + game.getAttributes().getMapObject().map(o -> " (" + o.getType() + ")").orElse(""), false));
		this.getWinningTeam().ifPresent(winner -> {
			builder.setThumbnail(winner.getLogo());
			builder.setImage(winner.getIcon());
			builder.setColor(winner.getPrimaryColor());
		});
		builder.setFooter("ID: " + this.getId());
		return builder;
	}
	
	@NonNull
	private Optional<OverwatchCompetitor> getWinningTeam(){
		return this.getCompetitors().stream().filter(c -> Objects.nonNull(c) && Objects.equals(c.getId(), this.getWinner())).findFirst();
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	public boolean hasStarted(){
		return this.getActualStartDate().orElse(this.getStartDate()).isBefore(ZonedDateTime.now());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		OverwatchMatch that = (OverwatchMatch) o;
		return id == that.id;
	}
	
	@Override
	public String toString(){
		return this.getId() + " (" + this.getVsCompetitorsNames() + ')';
	}
	
	public List<String> getCompetitorsNames(){
		return this.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.toList());
	}
	
	public String getVsCompetitorsNames(){
		return this.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.joining(" vs "));
	}
	
	@NonNull
	private Optional<ZonedDateTime> getActualStartDate(){
		return Optional.ofNullable(this.actualStartDate);
	}
	
	@NonNull
	public Optional<ZonedDateTime> getActualEndDate(){
		return Optional.ofNullable(this.actualEndDate);
	}
}
