package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryResponse {
	@NotNull
	private List<UserSeriesHistory> shows = new LinkedList<>();
	@NotNull
	private List<UserAnimeHistory> anime = new LinkedList<>();
	@NotNull
	private List<UserMovieHistory> movies = new LinkedList<>();
}
