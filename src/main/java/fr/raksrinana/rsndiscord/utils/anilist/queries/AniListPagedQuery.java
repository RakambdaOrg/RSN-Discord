package fr.raksrinana.rsndiscord.utils.anilist.queries;

import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.entities.Member;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListPagedQuery<T>{
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
	
	@Nonnull
	List<T> parseResult(@Nonnull JSONObject json) throws Exception;
	
	int getNextPage();
}
