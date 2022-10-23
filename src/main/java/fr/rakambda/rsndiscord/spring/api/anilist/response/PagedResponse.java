package fr.rakambda.rsndiscord.spring.api.anilist.response;

import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public sealed abstract class PagedResponse permits MediaListPagedResponse, NotificationPagedResponse{
	private PageInfo pageInfo;
}
