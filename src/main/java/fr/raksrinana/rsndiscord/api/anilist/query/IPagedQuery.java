package fr.raksrinana.rsndiscord.api.anilist.query;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.log.Log;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.Optional.ofNullable;

public interface IPagedQuery<T>{
	int PER_PAGE = 150;
	
	static String pagedQuery(String additionalParams, String query){
		return """
				query($page: Int, $perPage: Int%s){
				    Page (page: $page, perPage: $perPage) {
				        pageInfo {
				            total
				            currentPage
				            lastPage
				            hasNextPage
				            perPage
				        }
				        %s
				    }
				}""".formatted(additionalParams, query);
	}
	
	@NotNull
	default Set<T> getResult(@NotNull Member member) throws Exception{
		var elements = new HashSet<T>();
		var hasNext = true;
		
		while(hasNext && isFetchNextPage()){
			var json = AniListApi.postQuery(member, getQuery(), getParameters(getNextPage()));
			hasNext = json.getJSONObject("data")
					.getJSONObject("Page")
					.getJSONObject("pageInfo")
					.getBoolean("hasNextPage");
			elements.addAll(parseResult(json));
		}
		return elements;
	}
	
	default boolean isFetchNextPage(){
		return true;
	}
	
	@NotNull
	String getQuery();
	
	@NotNull
	JSONObject getParameters(int page);
	
	int getNextPage();
	
	@NotNull
	default List<T> parseResult(@NotNull JSONObject json){
		var changes = new ArrayList<T>();
		for(var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray(getPageElementName())){
			try{
				var changeJSONObj = (JSONObject) change;
				if(!changeJSONObj.isEmpty()){
					ofNullable(buildChange(changeJSONObj)).ifPresent(changes::add);
				}
				else{
					Log.getLogger(null).trace("Skipped AniList object, json: {}", change);
				}
			}
			catch(Exception e){
				Log.getLogger(null).error("Error building {} object, json was {}", getPageElementName(), change, e);
			}
		}
		return changes;
	}
	
	@NotNull
	String getPageElementName();
	
	@Nullable
	T buildChange(@NotNull JSONObject change) throws Exception;
}
