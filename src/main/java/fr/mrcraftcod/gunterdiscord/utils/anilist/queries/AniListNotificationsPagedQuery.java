package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListNotificationsPagedQuery implements AniListPagedQuery<AniListAiringNotification>{
	private static final Logger LOGGER = LoggerFactory.getLogger(AniListNotificationsPagedQuery.class);
	private static final String QUERY_NOTIFICATIONS = AniListPagedQuery.pagedQuery("", "notifications{\n" + "... on " + AniListAiringNotification.getQuery() + "}\n");
	
	private final JSONObject variables;
	private final Date date;
	
	public AniListNotificationsPagedQuery(final int userId, final int date){
		this.date = new Date(date * 1000L);
		this.variables = new JSONObject();
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", 50);
	}
	
	@Override
	public JSONObject getParameters(){
		return this.variables;
	}
	
	@Override
	public String getQuery(){
		return QUERY_NOTIFICATIONS;
	}
	
	@Override
	public List<AniListAiringNotification> parseResult(final JSONObject json) throws Exception{
		final var changes = new ArrayList<AniListAiringNotification>();
		for(final var change : json.getJSONObject("data").getJSONObject("Page").getJSONArray("notifications")){
			try{
				final var changeObj = buildChange((JSONObject) change);
				if(changeObj.getDate().after(this.date)){
					changes.add(changeObj);
				}
			}
			catch(final Exception e){
				LOGGER.error("Error building AniListAiringNotification object, json was {}", change, e);
			}
		}
		return changes;
	}
	
	private AniListAiringNotification buildChange(final JSONObject change) throws Exception{
		return AniListAiringNotification.buildFromJSON(change);
	}
}
