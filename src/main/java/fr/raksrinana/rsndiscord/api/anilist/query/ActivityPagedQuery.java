package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.list.IListActivity;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.time.ZonedDateTime;

public class ActivityPagedQuery implements IPagedQuery<IListActivity>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $userID: Int, $date: Int", """
			activities(userId: $userID, createdAt_greater: $date){
			    ... on %s
			}""".formatted(IListActivity.getQUERY()));
	private final JSONObject variables;
	private int currentPage = 0;
	
	public ActivityPagedQuery(final int userId, final ZonedDateTime date){
		final var second = date.toEpochSecond();
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		this.variables.put("date", second >= 0 ? second : 0);
	}
	
	@Override
	@NonNull
	public String getQuery(){
		return QUERY;
	}
	
	@Override
	@NonNull
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public int getNextPage(){
		return ++this.currentPage;
	}
	
	@NonNull
	@Override
	public String getPageElementName(){
		return "activities";
	}
	
	@NonNull
	public IListActivity buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(IListActivity.class).readValue(change.toString());
	}
}
