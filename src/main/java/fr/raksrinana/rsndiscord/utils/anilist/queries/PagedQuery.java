package fr.raksrinana.rsndiscord.utils.anilist.queries;

import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.DatedObject;
import fr.raksrinana.rsndiscord.utils.log.Log;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Member;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface PagedQuery<T>{
	int PER_PAGE = 50;
	
	static String pagedQuery(final String additionalParams, final String query){
		return "query($page: Int, $perPage: Int" + additionalParams + "){\n" + "  Page (page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + query + "  }\n" + "}";
	}
	
	@Nonnull
	default List<T> getResult(@Nonnull final Member member) throws Exception{
		final var elements = new ArrayList<T>();
		var hasNext = true;
		while(hasNext){
			final var json = AniListUtils.getQuery(member, this.getQuery(), this.getParameters(this.getNextPage()));
			hasNext = json.getJSONObject("data").getJSONObject("Page").getJSONObject("pageInfo").getBoolean("hasNextPage");
			elements.addAll(this.parseResult(json));
		}
		return elements;
	}
	
	@Nonnull
	String getQuery();
	
	@Nonnull
	JSONObject getParameters(int page);
	
	int getNextPage();
	
	@Nonnull
	default List<T> parseResult(@Nonnull final JSONObject json){
		final var changes = new ArrayList<T>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray(this.getPageElementName())){
			try{
				final var changeJSONObj = (JSONObject) change;
				if(!changeJSONObj.isEmpty()){
					Optional.ofNullable(this.buildChange(changeJSONObj)).ifPresent(changeObj -> {
						if(Objects.nonNull(this.getBaseDate()) && changeObj instanceof DatedObject){
							if(((DatedObject) changeObj).getDate().isAfter(this.getBaseDate())){
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
	
	@Nonnull
	String getPageElementName();
	
	@Nullable
	T buildChange(@Nonnull final JSONObject change) throws Exception;
	
	@Nullable
	LocalDateTime getBaseDate();
}
