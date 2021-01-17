package fr.raksrinana.rsndiscord.api.trakt.requests.users;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.users.history.UserHistory;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPagedGetRequest;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Objects.nonNull;

public class UserHistoryPagedGetRequest implements ITraktPagedGetRequest<UserHistory>{
	private final int page;
	private final ZonedDateTime startDate;
	private final ZonedDateTime endDate;
	private final String username;
	
	public UserHistoryPagedGetRequest(@NotNull String username, int page){
		this(username, page, null);
	}
	
	public UserHistoryPagedGetRequest(@NotNull String username, int page, ZonedDateTime startDate){
		this(username, page, startDate, null);
	}
	
	public UserHistoryPagedGetRequest(@NotNull String username, int page, ZonedDateTime startDate, ZonedDateTime endDate){
		this.username = username;
		this.page = page;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	@Override
	@NotNull
	public ITraktPagedGetRequest<UserHistory> getForPage(int page){
		return new UserHistoryPagedGetRequest(username, page);
	}
	
	@Override
	public int getPage(){
		return page;
	}
	
	@Override
	@NotNull
	public GenericType<Set<UserHistory>> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public GetRequest getRequest(){
		var request = Unirest.get(TraktApi.API_URL + "/users/{username}/history")
				.routeParam("username", username)
				.queryString("extended", "full")
				.queryString("page", getPage())
				.queryString("limit", getLimit());
		if(nonNull(startDate)){
			request.queryString("start_at", startDate.withZoneSameInstant(ZoneId.of("UTC")).format(ISO_DATE_TIME));
		}
		if(nonNull(endDate)){
			request.queryString("end_at", endDate.withZoneSameInstant(ZoneId.of("UTC")).format(ISO_DATE_TIME));
		}
		return request;
	}
	
	@Override
	public int getLimit(){
		return 5;
	}
}
