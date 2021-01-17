package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.airing.AiringSchedule;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AiringSchedulePagedQuery implements IPagedQuery<AiringSchedule>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $mediaID: Int", AiringSchedule.QUERY);
	
	private final JSONObject variables;
	private int currentPage = 0;
	
	public AiringSchedulePagedQuery(int mediaId){
		variables = new JSONObject();
		variables.put("mediaID", mediaId);
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
		return "airingSchedules";
	}
	
	@Nullable
	public AiringSchedule buildChange(@NotNull JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AiringSchedule.class).readValue(change.toString());
	}
}
