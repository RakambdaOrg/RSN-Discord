package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchWeek implements Comparable<OverwatchWeek>{
	@JsonProperty("id")
	private int id;
	@JsonProperty("startDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startDate;
	@JsonProperty("endDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime endDate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("matches")
	private List<OverwatchMatch> matches = new ArrayList<>();
	@JsonProperty("events")
	private List<OverwatchEvent> events;
	
	public boolean hasEnded(){
		return this.getMatches().stream().allMatch(OverwatchMatch::hasEnded);
	}
	
	public List<OverwatchMatch> getMatches(){
		return this.matches;
	}
	
	public boolean hasStarted(){
		return this.getMatches().stream().anyMatch(OverwatchMatch::hasStarted);
	}
	
	@Override
	public int compareTo(@Nonnull OverwatchWeek overwatchWeek){
		return this.getStartDate().compareTo(overwatchWeek.getStartDate());
	}
	
	private LocalDateTime getStartDate(){
		return this.startDate;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof OverwatchWeek && Objects.equals(this.getId(), ((OverwatchWeek) obj).getId());
	}
	
	private int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Optional<OverwatchMatch> getCurrentMatch(){
		return this.getMatches().stream().filter(w -> !w.hasEnded()).filter(OverwatchMatch::hasStarted).sorted().findFirst();
	}
	
	public Optional<OverwatchMatch> getNextMatch(){
		return this.getMatches().stream().filter(s -> !s.hasStarted()).sorted().findFirst();
	}
}
