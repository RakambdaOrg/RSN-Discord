package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public sealed class UserSeriesHistory extends UserHistory permits UserAnimeHistory {
	@NotNull
	private Show show;
	@Nullable
	@JsonProperty("last_watched")
	private String lastWatched;
	@Nullable
	@JsonProperty("next_to_watch")
	private String nextToWatch;
	@Nullable
	@JsonProperty("watched_episodes_count")
	private Integer watchedEpisodesCount;
	@Nullable
	@JsonProperty("total_episodes_count")
	private Integer totalEpisodesCount;
	@Nullable
	@JsonProperty("not_aired_episodes_count")
	private Integer notAiredEpisodesCount;
	
	@Override
	@NotNull
	public Long getId(){
		return show.getIds().getSimkl();
	}
}
