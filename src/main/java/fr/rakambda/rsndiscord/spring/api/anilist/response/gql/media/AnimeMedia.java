package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("ANIME")
public final class AnimeMedia extends Media{
	private Integer episodes;
	
	@Override
	public MediaType getType(){
		return MediaType.ANIME;
	}
}
