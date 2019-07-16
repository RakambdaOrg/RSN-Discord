package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.OverwatchIDTeamDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.OverwatchIDTournamentDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.*;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.bracket.OverwatchBracket;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.game.OverwatchGame;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchMatch{
	@JsonProperty("id")
	private int id;
	@JsonProperty("competitors")
	private List<OverwatchCompetitor> competitors = new ArrayList<>();
	@JsonProperty("scores")
	private List<OverwatchScore> scores = new ArrayList<>();
	@JsonProperty("conclusionValue")
	private int conclusionValue;
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
	private List<OverwatchGame> games;
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
	@JsonDeserialize(using = OverwatchIDTournamentDeserializer.class)
	private int tournament;
	@JsonProperty("broadcastChannels")
	private List<OverwatchBroadcastChannel> broadcastChannels;
	
	public boolean hasEnded(){
		return this.getActualEndDate().orElse(this.getEndDate()).isBefore(LocalDateTime.now());
	}
	
	@Nonnull
	public Optional<LocalDateTime> getActualEndDate(){
		return Optional.ofNullable(this.actualEndDate);
	}
	
	private LocalDateTime getEndDate(){
		return this.endDateTS;
	}
	
	public boolean hasStarted(){
		return this.getActualStartDate().orElse(getStartDate()).isBefore(LocalDateTime.now());
	}
	
	@Nonnull
	public Optional<LocalDateTime> getActualStartDate(){
		return Optional.ofNullable(this.actualStartDate);
	}
	
	public LocalDateTime getStartDate(){
		return this.startDateTS;
	}
	
	public List<OverwatchCompetitor> getCompetitors(){
		return this.competitors;
	}
	
	public List<OverwatchScore> getScores(){
		return this.scores;
	}
}
