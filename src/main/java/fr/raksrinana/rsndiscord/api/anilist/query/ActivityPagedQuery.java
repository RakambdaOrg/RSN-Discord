package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.list.ListActivity;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

public class ActivityPagedQuery implements IPagedQuery<ListActivity>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $userID: Int, $date: Int", """
			activities(userId: $userID, createdAt_greater: $date){
			    ... on %s
			}""".formatted(ListActivity.QUERY));
	
	private final JSONObject variables;
	private int currentPage = 0;
	
	public ActivityPagedQuery(int userId, @NotNull ZonedDateTime date){
		var second = date.toEpochSecond();
		variables = new JSONObject();
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", PER_PAGE);
		variables.put("date", second >= 0 ? second : 0);
	}
	
	@Override
	@NotNull
	public String getQuery(){
		return QUERY;
	}
	
	@Override
	@NotNull
	public JSONObject getParameters(int page){
		variables.put("page", page);
		return variables;
	}
	
	@Override
	public int getNextPage(){
		return ++currentPage;
	}
	
	@NotNull
	@Override
	public String getPageElementName(){
		return "activities";
	}
	
	@Nullable
	public ListActivity buildChange(@NotNull JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(ListActivity.class).readValue(change.toString());
	}
}
