package fr.rakambda.rsndiscord.spring.api.anilist.response.gql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo{
	private Long total;
	private Long currentPage;
	private Long lastPage;
	private Boolean hasNextPage;
	private Long perPage;
}
