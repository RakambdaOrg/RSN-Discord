package fr.raksrinana.rsndiscord.utils.overwatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.USADateStringDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.OverwatchStage;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.tournament.OverwatchTournament;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchData{
	@JsonProperty("id")
	private String id;
	@JsonProperty("startDate")
	@JsonDeserialize(using = USADateStringDeserializer.class)
	private LocalDate startDate;
	@JsonProperty("endDate")
	@JsonDeserialize(using = USADateStringDeserializer.class)
	private LocalDate endDate;
	@JsonProperty("endDateMS")
	private long endDateMs;
	@JsonProperty("seriesId")
	private int seriesId;
	@JsonProperty("stages")
	private List<OverwatchStage> stages = new ArrayList<>();
	
	public Optional<OverwatchStage> getStageOfTournament(final OverwatchTournament tournament){
		return this.getStages().stream().filter(s -> s.getTournaments().stream().anyMatch(t -> Objects.equals(t, tournament))).findFirst();
	}
	
	@Nonnull
	public List<OverwatchStage> getStages(){
		return this.stages;
	}
	
	public List<OverwatchMatch> getMatchesOfTournament(final OverwatchTournament tournament){
		return this.getStages().stream().flatMap(s -> s.getMatches().stream()).filter(m -> Objects.equals(m.getTournament(), tournament)).collect(Collectors.toList());
	}
	
	@Nonnull
	public Optional<OverwatchStage> getCurrentStage(){
		return this.getStages().stream().filter(OverwatchStage::isEnabled).filter(s -> !s.hasEnded()).filter(OverwatchStage::hasStarted).sorted().findFirst();
	}
	
	public Optional<OverwatchStage> getNextStage(){
		return this.getStages().stream().filter(OverwatchStage::isEnabled).filter(s -> !s.hasStarted()).sorted().findFirst();
	}
}
