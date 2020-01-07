package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.time.LocalDateTime;

public class MediaListPagedQuery implements PagedQuery<MediaList>{
	private static final String QUERY = PagedQuery.pagedQuery(", $userID: Int", MediaList.getQUERY());
	private final JSONObject variables;
	private int nextPage = 0;
	
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
		return ++this.nextPage;
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
	
	@Override
	public LocalDateTime getBaseDate(){
		return null;
	}
}
