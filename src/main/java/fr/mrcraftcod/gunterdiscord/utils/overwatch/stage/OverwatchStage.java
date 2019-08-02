package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.tournament.OverwatchTournament;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchStage implements Comparable<OverwatchStage>{
	@JsonProperty("id")
	private int id;
	@JsonProperty("slug")
	private String slug;
	@JsonProperty("enabled")
	private boolean enabled;
	@JsonProperty("name")
	private String name;
	@JsonProperty("tournaments")
	private List<OverwatchTournament> tournaments = new ArrayList<>();
	@JsonProperty("matches")
	private List<OverwatchMatch> matches = new ArrayList<>();
	@JsonProperty("weeks")
	private List<OverwatchWeek> weeks = new ArrayList<>();
	
	@Override
	public int compareTo(@Nonnull final OverwatchStage overwatchStage){
		final var s2 = overwatchStage.getStartDate();
		return overwatchStage.getStartDate().map(d1 -> s2.map(d1::compareTo).orElse(-1)).orElseGet(() -> s2.isPresent() ? 1 : 0);
	}
	
	public Optional<OverwatchTournament> getCurrentTournament(){
		return this.getCurrentMatch().map(OverwatchMatch::getTournament);
	}
	
	private Optional<LocalDateTime> getStartDate(){
		return this.getMatches().stream().map(OverwatchMatch::getStartDate).sorted().findFirst();
	}
	
	public Optional<OverwatchWeek> getCurrentWeek(){
		return this.getWeeks().stream().filter(w -> !w.hasEnded()).filter(OverwatchWeek::hasStarted).sorted().findFirst();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof OverwatchStage && Objects.equals(this.getId(), ((OverwatchStage) obj).getId());
	}
	
	public List<OverwatchMatch> getMatches(){
		return this.matches;
	}
	
	public boolean hasStarted(){
		return this.getMatches().stream().anyMatch(OverwatchMatch::hasStarted);
	}
	
	public boolean hasEnded(){
		return this.getMatches().stream().allMatch(OverwatchMatch::hasEnded);
	}
	
	private Optional<OverwatchMatch> getCurrentMatch(){
		return this.getMatches().stream().filter(w -> !w.hasEnded()).filter(OverwatchMatch::hasStarted).sorted().findFirst();
	}
	
	public int getId(){
		return this.id;
	}
	
	public Optional<OverwatchTournament> getNextTournament(){
		return this.getNextMatch().map(OverwatchMatch::getTournament);
	}
	
	private Optional<OverwatchMatch> getNextMatch(){
		return this.getMatches().stream().filter(s -> !s.hasStarted()).sorted().findFirst();
	}
	
	public String getName(){
		return this.name;
	}
	
	public Optional<OverwatchWeek> getNextWeek(){
		return this.getWeeks().stream().filter(s -> !s.hasStarted()).sorted().findFirst();
	}
	
	public List<OverwatchTournament> getTournaments(){
		return this.tournaments;
	}
	
	public List<OverwatchWeek> getWeeks(){
		return this.weeks;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
}
