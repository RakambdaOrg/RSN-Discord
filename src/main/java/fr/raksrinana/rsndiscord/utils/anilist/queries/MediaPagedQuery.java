package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import kong.unirest.json.JSONObject;
import lombok.Getter;
import lombok.NonNull;
import java.time.ZonedDateTime;

public class MediaPagedQuery implements PagedQuery<Media>{
	@Getter
	private final String query;
	private final JSONObject variables;
	private int nextPage = 0;
	
	public MediaPagedQuery(final int mediaId){
		this.variables = new JSONObject();
		this.variables.put("mediaId", mediaId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		this.query = PagedQuery.pagedQuery(", $mediaId: Int", Media.getQueryWithId());
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
		return "media";
	}
	
	@NonNull
	public Media buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(Media.class).readValue(change.toString());
	}
	
	@Override
	public ZonedDateTime getBaseDate(){
		return null;
	}
}
