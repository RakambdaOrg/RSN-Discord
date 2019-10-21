package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import fr.raksrinana.rsndiscord.utils.log.Log;
import kong.unirest.json.JSONObject;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListNotificationsPagedQuery implements AniListPagedQuery<AniListAiringNotification>{
	private static final String QUERY_NOTIFICATIONS = AniListPagedQuery.pagedQuery("", "notifications{\n" + "... on " + AniListAiringNotification.getQuery() + "}\n");
	private final JSONObject variables;
	private final LocalDateTime date;
	private int nextPage = 0;
	
	public AniListNotificationsPagedQuery(final int userId, final LocalDateTime date){
		this.date = date;
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
	
	@Nonnull
	@Override
	public List<AniListAiringNotification> parseResult(@Nonnull final JSONObject json){
		final var changes = new ArrayList<AniListAiringNotification>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("notifications")){
			try{
				final var changeJSONObj = (JSONObject) change;
				if(!changeJSONObj.isEmpty()){
					final var changeObj = this.buildChange(changeJSONObj);
					if(changeObj.getDate().isAfter(this.date)){
						changes.add(changeObj);
					}
				}
				else{
					Log.getLogger(null).trace("Skipped AniList object, json: {}", change);
				}
			}
			catch(final Exception e){
				Log.getLogger(null).error("Error building AniListAiringNotification object, json was {}", change, e);
			}
		}
		return changes;
	}
	
	@Nonnull
	@Override
	public String getQuery(){
		return QUERY_NOTIFICATIONS;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@Nonnull
	private AniListAiringNotification buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(AniListAiringNotification.class).readValue(change.toString());
	}
}
