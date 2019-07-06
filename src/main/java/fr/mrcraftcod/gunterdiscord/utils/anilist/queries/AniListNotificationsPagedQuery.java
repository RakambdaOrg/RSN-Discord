package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListNotificationsPagedQuery implements AniListPagedQuery<AniListAiringNotification>{
	private static final String QUERY_NOTIFICATIONS = AniListPagedQuery.pagedQuery("", "notifications{\n" + "... on " + AniListAiringNotification.getQuery() + "}\n");
	
	private final JSONObject variables;
	private final Date date;
	private int nextPage = 0;
	
	public AniListNotificationsPagedQuery(final int userId, final long date){
		this.date = new Date(date * 1000L);
		this.variables = new JSONObject();
		this.variables.put("userID", userId);
		this.variables.put("page", 1);
		this.variables.put("perPage", 50);
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
				final var changeObj = buildChange((JSONObject) change);
				if(changeObj.getDate().after(this.date)){
					changes.add(changeObj);
				}
			}
			catch(final Exception e){
				getLogger(null).error("Error building AniListAiringNotification object, json was {}", change, e);
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
