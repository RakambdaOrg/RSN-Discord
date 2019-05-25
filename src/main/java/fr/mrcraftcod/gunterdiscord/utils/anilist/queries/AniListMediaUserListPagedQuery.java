package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaUserList;
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
public class AniListMediaUserListPagedQuery implements AniListPagedQuery<AniListMediaUserList>{
	private static final String QUERY_MEDIA_LIST = AniListPagedQuery.pagedQuery(", $userID: Int", AniListMediaUserList.getQuery());
	
	private final JSONObject variables;
	private int nextPage = 0;
	
	public AniListMediaUserListPagedQuery(final int userId){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", 50);
	}
	
	@Override
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public List<AniListMediaUserList> parseResult(final JSONObject json){
		final var changes = new ArrayList<AniListMediaUserList>();
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
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	private AniListMediaUserList buildChange(final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AniListMediaUserList.class).readValue(change.toString());
	}
}
