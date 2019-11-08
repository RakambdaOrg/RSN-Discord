package fr.raksrinana.rsndiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.OverwatchIDTeamDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.enums.*;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.bracket.OverwatchBracket;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.game.OverwatchGame;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.tournament.OverwatchTournament;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchMatch implements Comparable<OverwatchMatch>{
	@JsonProperty("id")
	private int id;
	@JsonProperty("competitors")
	private List<OverwatchCompetitor> competitors = new ArrayList<>();
	@JsonProperty("scores")
	private List<OverwatchScore> scores = new ArrayList<>();
	@JsonProperty("conclusionValue")
	private int conclusionValue = -1;
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
	@JsonProperty("games")
	private List<OverwatchGame> games = new ArrayList<>();
	@JsonProperty("clientHints")
	private List<String> clientHints; //TODO
	@JsonProperty("bracket")
	private OverwatchBracket bracket;
	@JsonProperty("dateCreated")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime dateCreated;
	@JsonProperty("flags")
	private List<String> flags; //TODO
	@JsonProperty("handle")
	private String handle;
	@JsonProperty("competitorStatuses")
	private List<OverwatchCompetitorStatus> competitorStatuses;
	@JsonProperty("timeZone")
	private String timeZone;
	@JsonProperty("actualStartDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime actualStartDate;
	@JsonProperty("actualEndDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime actualEndDate;
	@JsonProperty("startDate")
	private String startDate;
	@JsonProperty("endDate")
	private String endDate;
	@JsonProperty("showStartTime")
	private boolean showStartTime;
	@JsonProperty("showEndTime")
	private boolean showEndTime;
	@JsonProperty("startDateTS")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startDateTS;
	@JsonProperty("endDateTS")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime endDateTS;
	@JsonProperty("youtubeId")
	private String youtubeId;
	@JsonProperty("wins")
	private List<Integer> wins;
	@JsonProperty("ties")
	private List<Integer> ties;
	@JsonProperty("losses")
	private List<Integer> losses;
	@JsonProperty("videos")
	private List<OverwatchVideo> videos;
	@JsonProperty("tournament")
	private OverwatchTournament tournament;
	@JsonProperty("broadcastChannels")
	private List<OverwatchBroadcastChannel> broadcastChannels;
	@JsonProperty("round")
	private int round;
	@JsonProperty("ordinal")
	private int ordinal;
	@JsonProperty("bestOf")
	private int bestOf = -1;
	@JsonProperty("attributesVersion")
	private String attributesVersion;
	
	public boolean hasEnded(){
		return this.state == OverwatchState.CONCLUDED;
	}
	
	@Override
	public int compareTo(@Nonnull final OverwatchMatch overwatchMatch){
		return this.getStartDate().compareTo(overwatchMatch.getStartDate());
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
		this.getGames().forEach(game -> builder.addField("Game " + game.getNumber(), game.getAttributes().getMapName() + ": " + game.getPoints().stream().map(Object::toString).collect(Collectors.joining(" - ")) + game.getAttributes().getMapObject().map(o -> " (" + o.getType() + ")").orElse(""), false));
		this.getWinningTeam().ifPresent(winner -> {
			builder.setThumbnail(winner.getLogo());
			builder.setImage(winner.getIcon());
			builder.setColor(winner.getPrimaryColor());
		});
		builder.setFooter("ID: " + this.getId());
		return builder;
	}
	
	@Nonnull
	public Optional<LocalDateTime> getActualEndDate(){
		return Optional.ofNullable(this.actualEndDate);
	}
	
	private String getYoutubeId(){
		return this.youtubeId;
	}
	
	public int getBestOf(){
		return this.bestOf;
	}
	
	private OverwatchState getState(){
		return this.state;
	}
	
	private OverwatchConclusionStrategy getConclusionStrategy(){
		return this.conclusionStrategy;
	}
	
	private List<OverwatchGame> getGames(){
		return this.games;
	}
	
	@Nonnull
	private Optional<OverwatchCompetitor> getWinningTeam(){
		return this.getCompetitors().stream().filter(c -> Objects.nonNull(c) && Objects.equals(c.getId(), this.getWinner())).findFirst();
	}
	
	public int getConclusionValue(){
		return this.conclusionValue;
	}
	
	private LocalDateTime getEndDate(){
		return this.endDateTS;
	}
	
	public int getId(){
		return this.id;
	}
	
	public List<OverwatchScore> getScores(){
		return this.scores;
	}
	
	public boolean hasStarted(){
		return this.getActualStartDate().orElse(this.getStartDate()).isBefore(LocalDateTime.now());
	}
	
	@Nonnull
	private Optional<LocalDateTime> getActualStartDate(){
		return Optional.ofNullable(this.actualStartDate);
	}
	
	@Override
	public String toString(){
		return this.getId() + " (" + this.getVsCompetitorsNames() + ')';
	}
	
	public LocalDateTime getStartDate(){
		return this.startDateTS;
	}
	
	public OverwatchTournament getTournament(){
		return this.tournament;
	}
	
	public List<String> getCompetitorsNames(){
		return this.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.toList());
	}
	
	public String getVsCompetitorsNames(){
		return this.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.joining(" vs "));
	}
	
	public List<OverwatchCompetitor> getCompetitors(){
		return this.competitors;
	}
	
	private int getWinner(){
		return this.winner;
	}
}
