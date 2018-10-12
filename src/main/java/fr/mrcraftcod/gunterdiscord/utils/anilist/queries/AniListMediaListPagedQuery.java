package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaList;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListMediaListPagedQuery implements AniListPagedQuery<AniListMediaList>{
	private static final String QUERY_MEDIA_LIST = AniListPagedQuery.pagedQuery(", $userID: Int", AniListMediaList.getQuery());
	
	private final JSONObject variables;
	private int nextPage = 0;
	
	public AniListMediaListPagedQuery(final int userId){
		this.variables = new JSONObject();
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", 50);
	}
	
	@Override
	public int getNextPage(){
		return ++nextPage;
	}
	
	@Override
	public List<AniListMediaList> parseResult(final JSONObject json) throws Exception{
		final var changes = new ArrayList<AniListMediaList>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("mediaList")){
			try{
				changes.add(buildChange((JSONObject) change));
			}
			catch(final Exception e){
				getLogger(null).error("Error building AniListAiringNotification object, json was {}", change, e);
			}
		}
		return changes;
	}
	
	@Override
	public String getQuery(){
		return QUERY_MEDIA_LIST;
	}
	
	@Override
	public JSONObject getParameters(final int page){
		variables.put("page", page);
		return this.variables;
	}
	
	private AniListMediaList buildChange(final JSONObject change) throws Exception{
		return AniListMediaList.buildFromJSON(change);
	}
}
