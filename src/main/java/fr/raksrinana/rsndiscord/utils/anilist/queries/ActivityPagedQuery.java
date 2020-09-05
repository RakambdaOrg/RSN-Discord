package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.activity.list.ListActivity;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.time.ZonedDateTime;

public class ActivityPagedQuery implements PagedQuery<ListActivity>{
	private static final String QUERY = PagedQuery.pagedQuery(", $userID: Int, $date: Int", "activities(userId: $userID, createdAt_greater: $date){\n" + "... on " + ListActivity.getQUERY() + "\n}");
	private final JSONObject variables;
	private int currentPage = 0;
	
	public ActivityPagedQuery(final int userId, final ZonedDateTime date){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		final var s = date.toEpochSecond();
		this.variables.put("date", s >= 0 ? s : 0);
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
	public ListActivity buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(ListActivity.class).readValue(change.toString());
	}
}
