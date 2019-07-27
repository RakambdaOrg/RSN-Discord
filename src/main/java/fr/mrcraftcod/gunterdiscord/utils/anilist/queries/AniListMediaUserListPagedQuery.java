package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaUserList;
import org.json.JSONObject;
import javax.annotation.Nonnull;
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
	
	@Nonnull
	@Override
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Nonnull
	@Override
	public List<AniListMediaUserList> parseResult(@Nonnull final JSONObject json){
		final var changes = new ArrayList<AniListMediaUserList>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("mediaList")){
			try{
				final var obj = (JSONObject) change;
				if(obj.length() > 0){
					changes.add(buildChange(obj));
				}
				else{
					getLogger(null).trace("Skipped AniList object, json: {}", change);
				}
			}
			catch(final Exception e){
				getLogger(null).error("Error building AniListAiringNotification object, json was {}", change, e);
			}
		}
		return changes;
	}
	
	@Nonnull
	@Override
	public String getQuery(){
		return QUERY_MEDIA_LIST;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@Nonnull
	private AniListMediaUserList buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AniListMediaUserList.class).readValue(change.toString());
	}
}
