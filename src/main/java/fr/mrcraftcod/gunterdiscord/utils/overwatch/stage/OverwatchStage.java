package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.json.OverwatchIDTournamentDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
	@JsonDeserialize(contentUsing = OverwatchIDTournamentDeserializer.class)
	private List<Integer> tournaments;
	@JsonProperty("matches")
	private List<OverwatchMatch> matches = new ArrayList<>();
	@JsonProperty("weeks")
	private List<OverwatchWeek> weeks = new ArrayList<>();
	
	@Override
	public int compareTo(@Nonnull OverwatchStage overwatchStage){
		final var s2 = overwatchStage.getStartDate();
		return overwatchStage.getStartDate().map(d1 -> s2.map(d1::compareTo).orElse(-1)).orElseGet(() -> s2.isPresent() ? 1 : 0);
	}
	
	private Optional<LocalDateTime> getStartDate(){
		return getMatches().stream().map(OverwatchMatch::getStartDate).sorted().findFirst();
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
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof OverwatchStage && Objects.equals(this.getId(), ((OverwatchStage) obj).getId());
	}
	
	private int getId(){
		return this.id;
	}
	
	public Optional<OverwatchWeek> getCurrentWeek(){
		return this.getWeeks().stream().filter(w -> !w.hasActuallyEnded()).filter(OverwatchWeek::hasActuallyStarted).sorted().findFirst();
	}
	
	public String getName(){
		return this.name;
	}
	
	public Optional<OverwatchWeek> getNextWeek(){
		return this.getWeeks().stream().filter(s -> !s.hasActuallyStarted()).sorted().findFirst();
	}
	
	public List<OverwatchWeek> getWeeks(){
		return this.weeks;
	}
	
	public boolean isEnabled(){
		return this.enabled;
	}
}
