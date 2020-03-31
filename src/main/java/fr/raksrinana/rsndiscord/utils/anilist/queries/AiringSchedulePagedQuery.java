package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.airing.AiringSchedule;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.time.LocalDateTime;

public class AiringSchedulePagedQuery implements PagedQuery<AiringSchedule>{
	private static final String QUERY = PagedQuery.pagedQuery(", $mediaID: Int", AiringSchedule.getQUERY());
	private final JSONObject variables;
	private int nextPage = 0;
	
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
		return ++this.nextPage;
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
	
	@Override
	public LocalDateTime getBaseDate(){
		return null;
	}
}