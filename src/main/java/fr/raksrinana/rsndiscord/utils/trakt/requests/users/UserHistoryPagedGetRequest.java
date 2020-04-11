package fr.raksrinana.rsndiscord.utils.trakt.requests.users;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPagedGetRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import lombok.NonNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

public class UserHistoryPagedGetRequest implements TraktPagedGetRequest<UserHistory>{
	private final int page;
	private final ZonedDateTime startDate;
	private final ZonedDateTime endDate;
	private final String username;
	
	public UserHistoryPagedGetRequest(@NonNull String username){
		this(username, 1);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page){
		this(username, page, null);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page, ZonedDateTime startDate){
		this(username, page, startDate, null);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page, ZonedDateTime startDate, ZonedDateTime endDate){
		this.username = username;
		this.page = page;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	@Override
	public TraktPagedGetRequest<UserHistory> getForPage(int page){
		return new UserHistoryPagedGetRequest(this.username, page);
	}
	
	@Override
	public int getPage(){
		return this.page;
	}
	
	@Override
	public GenericType<Set<UserHistory>> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		final var request = Unirest.get(TraktUtils.API_URL + "/users/{username}/history").routeParam("username", this.username).queryString("extended", "full").queryString("page", getPage()).queryString("limit", getLimit());
		if(Objects.nonNull(startDate)){
			request.queryString("start_at", startDate.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME));
		}
		if(Objects.nonNull(endDate)){
			request.queryString("end_at", endDate.withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME));
		}
		return request;
	}
}
