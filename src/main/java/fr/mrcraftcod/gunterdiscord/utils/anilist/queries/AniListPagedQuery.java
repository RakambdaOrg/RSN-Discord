package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListPagedQuery<T>{
	static String pagedQuery(final String additionalParams, final String query){
		return "query($page: Int, $perPage: Int" + additionalParams + "){\n" + "  Page (page: $page, perPage: $perPage) {\n" + "    pageInfo {\n" + "      total\n" + "      currentPage\n" + "      lastPage\n" + "      hasNextPage\n" + "      perPage\n" + "    }\n" + query + "  }\n" + "}";
	}
	
	default List<T> getResult(final Member member) throws Exception{
		final var elements = new ArrayList<T>();
		var hasNext = true;
		while(hasNext){
			final var json = AniListUtils.getQuery(member, getQuery(), getParameters(getNextPage()));
			hasNext = json.getJSONObject("data").getJSONObject("Page").getJSONObject("pageInfo").getBoolean("hasNextPage");
			elements.addAll(parseResult(json));
		}
		return elements;
	}
	
	JSONObject getParameters(int page);
	
	List<T> parseResult(JSONObject json) throws Exception;
	
	String getQuery();
	
	int getNextPage();
}
