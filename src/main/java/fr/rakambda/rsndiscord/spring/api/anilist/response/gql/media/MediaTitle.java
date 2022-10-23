package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaTitle{
	private String userPreferred;
	private String romaji;
	private String english;
	private String nativeTitle;
}
