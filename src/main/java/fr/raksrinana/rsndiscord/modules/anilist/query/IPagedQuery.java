package fr.raksrinana.rsndiscord.modules.anilist.query;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.anilist.AniListUtils;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.util.*;

public interface IPagedQuery<T>{
	int PER_PAGE = 150;
	
	static String pagedQuery(final String additionalParams, final String query){
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
				}""".formatted(
				additionalParams,
				query
		);
	}
	
	@NonNull
	default Set<T> getResult(@NonNull final Member member) throws Exception{
		final var elements = new HashSet<T>();
		var hasNext = true;
		while(hasNext && isFetchNextPage()){
			final var json = AniListUtils.postQuery(member, this.getQuery(), this.getParameters(this.getNextPage()));
			hasNext = json.getJSONObject("data").getJSONObject("Page").getJSONObject("pageInfo").getBoolean("hasNextPage");
			elements.addAll(this.parseResult(json));
		}
		return elements;
	}
	
	default boolean isFetchNextPage(){
		return true;
	}
	
	@NonNull String getQuery();
	
	@NonNull JSONObject getParameters(int page);
	
	int getNextPage();
	
	@NonNull
	default List<T> parseResult(@NonNull final JSONObject json){
		final var changes = new ArrayList<T>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray(this.getPageElementName())){
			try{
				final var changeJSONObj = (JSONObject) change;
				if(!changeJSONObj.isEmpty()){
					Optional.ofNullable(this.buildChange(changeJSONObj)).ifPresent(changes::add);
				}
				else{
					Log.getLogger(null).trace("Skipped AniList object, json: {}", change);
				}
			}
			catch(final Exception e){
				Log.getLogger(null).error("Error building {} object, json was {}", this.getPageElementName(), change, e);
			}
		}
		return changes;
	}
	
	@NonNull String getPageElementName();
	
	T buildChange(@NonNull final JSONObject change) throws Exception;
}
