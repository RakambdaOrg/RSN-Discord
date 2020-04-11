package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.OverwatchMatch;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.tournament.OverwatchTournament;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.week.OverwatchWeek;
import lombok.Getter;
import lombok.NonNull;
import java.time.ZonedDateTime;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
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
	private final Set<OverwatchTournament> tournaments = new HashSet<>();
	@JsonProperty("matches")
	private final Set<OverwatchMatch> matches = new HashSet<>();
	@JsonProperty("weeks")
	private final List<OverwatchWeek> weeks = new LinkedList<>();
	
	@Override
	public int compareTo(@NonNull final OverwatchStage overwatchStage){
		final var s2 = overwatchStage.getStartDate();
		return overwatchStage.getStartDate().map(d1 -> s2.map(d1::compareTo).orElse(-1)).orElseGet(() -> s2.isPresent() ? 1 : 0);
	}
	
	private Optional<ZonedDateTime> getStartDate(){
		return this.getMatches().stream().map(OverwatchMatch::getStartDate).sorted().findFirst();
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof OverwatchStage && Objects.equals(this.getId(), ((OverwatchStage) obj).getId());
	}
	
	public Optional<OverwatchTournament> getCurrentTournament(){
		return this.getCurrentMatch().map(OverwatchMatch::getTournament);
	}
	
	public Optional<OverwatchWeek> getCurrentWeek(){
		return this.getWeeks().stream().filter(w -> !w.hasEnded()).filter(OverwatchWeek::hasStarted).sorted().findFirst();
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
	
	public Optional<OverwatchTournament> getNextTournament(){
		return this.getNextMatch().map(OverwatchMatch::getTournament);
	}
	
	private Optional<OverwatchMatch> getNextMatch(){
		return this.getMatches().stream().filter(s -> !s.hasStarted()).sorted().findFirst();
	}
	
	public Optional<OverwatchWeek> getNextWeek(){
		return this.getWeeks().stream().filter(s -> !s.hasStarted()).sorted().findFirst();
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
}
