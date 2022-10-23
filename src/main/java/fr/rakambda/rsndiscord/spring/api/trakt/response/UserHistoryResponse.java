package fr.rakambda.rsndiscord.spring.api.trakt.response;

import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.UserHistory;
import java.util.List;

public record UserHistoryResponse(
		List<UserHistory> userHistories,
		int page,
		int maxPage
){
}
