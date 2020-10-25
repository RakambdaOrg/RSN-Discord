package fr.raksrinana.rsndiscord.modules.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.modules.anilist.data.list.MediaList;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class MediaListPagedQuery implements IPagedQuery<MediaList>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $userID: Int", MediaList.getQUERY());
	private final JSONObject variables;
	private int currentPage = 0;
	
	public MediaListPagedQuery(final int userId){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
	}
	
	@NonNull
	@Override
	public String getQuery(){
		return QUERY;
	}
	
	@NonNull
	@Override
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
		return "mediaList";
	}
	
	@NonNull
	public MediaList buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(MediaList.class).readValue(change.toString());
	}
}
