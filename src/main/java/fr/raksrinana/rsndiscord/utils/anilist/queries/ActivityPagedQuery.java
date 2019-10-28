package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.activity.list.ListActivity;
import kong.unirest.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class ActivityPagedQuery implements PagedQuery<ListActivity>{
	private static final String QUERY_FEED = PagedQuery.pagedQuery(", $userID: Int, $date: Int", "activities(userId: $userID, createdAt_greater: $date){\n" + "... on " + ListActivity.getQuery() + "\n}");
	private final JSONObject variables;
	private int nextPage = 0;
	
	public ActivityPagedQuery(final int userId, final LocalDateTime date){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		final var s = date.atZone(ZoneId.of("UTC")).toEpochSecond();
		this.variables.put("date", s >= 0 ? s : 0);
	}
	
	@Override
	@Nonnull
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Nullable
	@Override
	public LocalDateTime getBaseDate(){
		return null;
	}
	
	@Nonnull
	@Override
	public String getPageElementName(){
		return "activities";
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@Override
	@Nonnull
	public String getQuery(){
		return QUERY_FEED;
	}
	
	@Nonnull
	public ListActivity buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(ListActivity.class).readValue(change.toString());
	}
}
