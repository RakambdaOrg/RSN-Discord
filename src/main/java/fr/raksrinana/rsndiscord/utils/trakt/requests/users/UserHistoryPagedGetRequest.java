package fr.raksrinana.rsndiscord.utils.trakt.requests.users;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPagedGetRequest;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory;
import kong.unirest.GenericType;
import lombok.NonNull;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UserHistoryPagedGetRequest implements TraktPagedGetRequest<UserHistory>{
	private final int page;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private String username;
	
	public UserHistoryPagedGetRequest(@NonNull String username){
		this(username, 1);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page){
		this(username, page, null);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page, LocalDateTime startDate){
		this(username, page, startDate, null);
	}
	
	public UserHistoryPagedGetRequest(@NonNull String username, int page, LocalDateTime startDate, LocalDateTime endDate){
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
	public @NonNull String getEndpoint(){
		return MessageFormat.format("/users/{0}/history", username);
	}
	
	@Override
	public GenericType<? extends Set<UserHistory>> getResultClass(){
		return new GenericType<>(){};
	}
	
	@Override
	public Map<String, String> getParameters(){
		final var parameters = new HashMap<String, String>();
		parameters.put("extended", "full");
		if(Objects.nonNull(startDate)){
			parameters.put("start_at", startDate.format(DateTimeFormatter.ISO_DATE_TIME));
		}
		if(Objects.nonNull(endDate)){
			parameters.put("end_at", endDate.format(DateTimeFormatter.ISO_DATE_TIME));
		}
		return parameters;
	}
}
