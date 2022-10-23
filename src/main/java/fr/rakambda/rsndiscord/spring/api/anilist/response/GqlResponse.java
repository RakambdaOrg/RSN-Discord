package fr.rakambda.rsndiscord.spring.api.anilist.response;

import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.Error;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class GqlResponse<T>{
	private T data;
	private List<Error> errors = new ArrayList<>();
}
