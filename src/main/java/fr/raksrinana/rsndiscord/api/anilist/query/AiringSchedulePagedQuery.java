package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.airing.AiringSchedule;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class AiringSchedulePagedQuery implements IPagedQuery<AiringSchedule>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $mediaID: Int", AiringSchedule.getQUERY());
	private final JSONObject variables;
	private int currentPage = 0;
	
	public AiringSchedulePagedQuery(final int mediaId){
		this.variables = new JSONObject();
		this.variables.put("mediaID", mediaId);
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
		return "airingSchedules";
	}
	
	@NonNull
	public AiringSchedule buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AiringSchedule.class).readValue(change.toString());
	}
}
