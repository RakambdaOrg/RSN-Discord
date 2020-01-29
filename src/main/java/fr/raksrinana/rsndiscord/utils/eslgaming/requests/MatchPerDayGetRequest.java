package fr.raksrinana.rsndiscord.utils.eslgaming.requests;

import fr.raksrinana.rsndiscord.utils.eslgaming.ESLGetRequest;
import fr.raksrinana.rsndiscord.utils.eslgaming.ESLRequestResult;
import fr.raksrinana.rsndiscord.utils.eslgaming.ESLUtils;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.MatchDay;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.ESLRegion;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import lombok.Setter;

@Setter
public class MatchPerDayGetRequest implements ESLGetRequest<ESLRequestResult<MatchDay>>{
	private int parentPid;
	private String pids = "";
	private String language = "en";
	private String type = "undefined";
	private int offset = -1;
	private String rematches = "undefined";
	private String maxDays = "undefined";
	private String status = "";
	
	public MatchPerDayGetRequest(ESLRegion region){
		this(region.getId());
	}
	
	public MatchPerDayGetRequest(int parentPid){
		this.parentPid = parentPid;
	}
	
	@Override
	public GenericType<ESLRequestResult<MatchDay>> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(ESLUtils.API_URL + "/match/perday").queryString("parentpid", parentPid).queryString("pids", pids).queryString("lang", language).queryString("status", status).queryString("type", type).queryString("offset", offset).queryString("rematches", rematches).queryString("maxdays", maxDays).queryString("", (Object) null);
	}
}
