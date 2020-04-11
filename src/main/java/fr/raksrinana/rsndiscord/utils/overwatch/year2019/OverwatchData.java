package fr.raksrinana.rsndiscord.utils.overwatch.year2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.USALocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.OverwatchStage;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.OverwatchMatch;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.tournament.OverwatchTournament;
import lombok.Getter;
import lombok.NonNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchData{
	@JsonProperty("id")
	private String id;
	@JsonProperty("startDate")
	@JsonDeserialize(using = USALocalDateDeserializer.class)
	private LocalDate startDate;
	@JsonProperty("endDate")
	@JsonDeserialize(using = USALocalDateDeserializer.class)
	private LocalDate endDate;
	@JsonProperty("endDateMS")
	private long endDateMs;
	@JsonProperty("seriesId")
	private int seriesId;
	@JsonProperty("stages")
	private final Set<OverwatchStage> stages = new HashSet<>();
	
	public Optional<OverwatchStage> getStageOfTournament(final OverwatchTournament tournament){
		return this.getStages().stream().filter(s -> s.getTournaments().stream().anyMatch(t -> Objects.equals(t, tournament))).findFirst();
	}
	
	public Set<OverwatchMatch> getMatchesOfTournament(final OverwatchTournament tournament){
		return this.getStages().stream().flatMap(s -> s.getMatches().stream()).filter(m -> Objects.equals(m.getTournament(), tournament)).collect(Collectors.toSet());
	}
	
	@NonNull
	public Set<OverwatchStage> getStages(){
		return this.stages;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	public Optional<OverwatchStage> getNextStage(){
		return this.getStages().stream().filter(OverwatchStage::isEnabled).filter(s -> !s.hasStarted()).sorted().findFirst();
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof OverwatchData && Objects.equals(this.getId(), ((OverwatchData) obj).getId());
	}
	
	@NonNull
	public Optional<OverwatchStage> getCurrentStage(){
		return this.getStages().stream().filter(OverwatchStage::isEnabled).filter(s -> !s.hasEnded()).filter(OverwatchStage::hasStarted).sorted().findFirst();
	}
}
