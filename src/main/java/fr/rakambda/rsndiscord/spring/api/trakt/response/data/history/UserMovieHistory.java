package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("movie")
public final class UserMovieHistory extends UserHistory{
	private Movie movie;
	
	@Override
	@NotNull
	public TraktMediaType getType(){
		return TraktMediaType.MOVIE;
	}
}
