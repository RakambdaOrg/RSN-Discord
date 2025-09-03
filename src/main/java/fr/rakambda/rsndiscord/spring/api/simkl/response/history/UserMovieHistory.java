package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UserMovieHistory extends UserHistory{
	@NonNull
	private Movie movie;
	
	@Override
	@NonNull
	public Long getId(){
		return movie.getIds().getSimkl();
	}
}
