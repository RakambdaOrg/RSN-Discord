package fr.raksrinana.rsndiscord.utils.anilist.queries;

import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.log.Log;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.time.LocalDateTime;
import java.util.*;

public interface PagedQuery<T>{
	int PER_PAGE = 150;
	
	static String pagedQuery(final String additionalParams, final String query){
		return "query($page: Int, $perPage: Int" + additionalParams + "){\n" + "  Page (page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + query + "  }\n" + "}";
	}
	
	@NonNull
	default Set<T> getResult(@NonNull final Member member) throws Exception{
		final var elements = new HashSet<T>();
		var hasNext = true;
		while(hasNext){
			final var json = AniListUtils.postQuery(member, this.getQuery(), this.getParameters(this.getNextPage()));
			hasNext = json.getJSONObject("data").getJSONObject("Page").getJSONObject("pageInfo").getBoolean("hasNextPage");
			elements.addAll(this.parseResult(json));
		}
		return elements;
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
					Optional.ofNullable(this.buildChange(changeJSONObj)).ifPresent(changeObj -> {
						if(Objects.nonNull(this.getBaseDate()) && changeObj instanceof AnilistDatedObject){
							if(((AnilistDatedObject) changeObj).getDate().isAfter(this.getBaseDate())){
								changes.add(changeObj);
							}
						}
						else{
							changes.add(changeObj);
						}
					});
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
	
	LocalDateTime getBaseDate();
}
