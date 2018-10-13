package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import org.json.JSONObject;
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
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", 50);
		variables.put("date", date);
	}
	
	@Override
	public int getNextPage(){
		return ++nextPage;
	}
	
	@Override
	public String getQuery(){
		return QUERY_FEED;
	}
	
	@Override
	public JSONObject getParameters(final int page){
		variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public List<AniListListActivity> parseResult(final JSONObject json) throws Exception{
		final var changes = new ArrayList<AniListListActivity>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("activities")){
			changes.add(buildChange((JSONObject) change));
		}
		return changes;
	}
	
	private AniListListActivity buildChange(final JSONObject change) throws Exception{
		return AniListListActivity.buildFromJSON(change);
	}
}
