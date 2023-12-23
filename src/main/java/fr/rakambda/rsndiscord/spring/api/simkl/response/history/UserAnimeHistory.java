package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UserAnimeHistory extends UserSeriesHistory {
	@Nullable
	@JsonProperty("anime_type")
	private String animeType;
}
