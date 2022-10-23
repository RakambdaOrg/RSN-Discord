package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaCoverImage{
	private URL large;
}
