package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListListActivityPagedQuery implements AniListPagedQuery<AniListListActivity>{
	private static final String QUERY_FEED = AniListPagedQuery.pagedQuery(", $userID: Int, $date: Int", "activities(userId: $userID, createdAt_greater: $date){\n" + "... on " + AniListListActivity.getQuery() + "}\n");
	private final JSONObject variables;
	private int nextPage = 0;
	
	public AniListListActivityPagedQuery(final int userId, final int date){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", 50);
		this.variables.put("date", date);
	}
	
	@Override
	@Nonnull
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	@Nonnull
	public List<AniListListActivity> parseResult(@Nonnull final JSONObject json) throws Exception{
		final var changes = new ArrayList<AniListListActivity>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("activities")){
			changes.add(buildChange((JSONObject) change));
		}
		return changes;
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
	private AniListListActivity buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AniListListActivity.class).readValue(change.toString());
	}
}
