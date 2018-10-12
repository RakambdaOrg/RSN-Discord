package fr.mrcraftcod.gunterdiscord.utils.anilist.queries;

import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
	
	public AniListNotificationsPagedQuery(final int userId, final int date){
		this.date = new Date(date * 1000L);
		this.variables = new JSONObject();
		variables.put("userID", userId);
		variables.put("page", 1);
		variables.put("perPage", 50);
	}
	
	@Override
	public int getNextPage(){
		return ++nextPage;
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
				if(Objects.nonNull(changeObj) && changeObj.getDate().after(this.date)){
					changes.add(changeObj);
				}
			}
			catch(final Exception e){
				getLogger(null).error("Error building AniListAiringNotification object, json was {}", change, e);
			}
		}
		return changes;
	}
	
	@Override
	public JSONObject getParameters(final int page){
		variables.put("page", page);
		return this.variables;
	}
	
	private AniListAiringNotification buildChange(final JSONObject change) throws Exception{
		if(change.keySet().size() > 0){
			return AniListAiringNotification.buildFromJSON(change);
		}
		return null;
	}
}
