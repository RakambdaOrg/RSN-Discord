package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MediaListPagedQuery implements IPagedQuery<MediaList>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $userID: Int", MediaList.QUERY);
	
	private final JSONObject variables;
	private int currentPage = 0;
	
	public MediaListPagedQuery(int userId){
		variables = new JSONObject();
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", PER_PAGE);
	}
	
	@NotNull
	@Override
	public String getQuery(){
		return QUERY;
	}
	
	@NotNull
	@Override
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
		return "mediaList";
	}
	
	@Nullable
	public MediaList buildChange(@NotNull JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(MediaList.class).readValue(change.toString());
	}
}
