package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import kong.unirest.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class MediaListPagedQuery implements PagedQuery<MediaList>{
	private static final String QUERY_MEDIA_LIST = PagedQuery.pagedQuery(", $userID: Int", MediaList.getQuery());
	private final JSONObject variables;
	private int nextPage = 0;
	
	public MediaListPagedQuery(final int userId){
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
	}
	
	@Nonnull
	@Override
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Nullable
	@Override
	public LocalDateTime getBaseDate(){
		return null;
	}
	
	@Nonnull
	@Override
	public String getPageElementName(){
		return "mediaList";
	}
	
	@Nonnull
	@Override
	public String getQuery(){
		return QUERY_MEDIA_LIST;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@Nonnull
	public MediaList buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(MediaList.class).readValue(change.toString());
	}
}
