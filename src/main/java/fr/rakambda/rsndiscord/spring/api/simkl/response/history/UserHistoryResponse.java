package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryResponse {
	@NonNull
	private List<UserSeriesHistory> shows = new LinkedList<>();
	@NonNull
	private List<UserAnimeHistory> anime = new LinkedList<>();
	@NonNull
	private List<UserMovieHistory> movies = new LinkedList<>();
}
