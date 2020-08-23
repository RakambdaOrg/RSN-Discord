package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.Notification;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.NotificationType;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.util.List;

public class NotificationsPagedQuery implements PagedQuery<Notification>{
	private static final String QUERY = PagedQuery.pagedQuery(", $type_in: [NotificationType]", Notification.getQuery());
	private final JSONObject variables;
	private int nextPage = 0;
	
	public NotificationsPagedQuery(){
		this.variables = new JSONObject();
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		this.variables.put("type_in", List.of(NotificationType.AIRING.name(), NotificationType.RELATED_MEDIA_ADDITION.name()));
	}
	
	@NonNull
	@Override
	public String getQuery(){
		return QUERY;
	}
	
	@NonNull
	@Override
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@NonNull
	@Override
	public String getPageElementName(){
		return "notifications";
	}
	
	@NonNull
	public Notification buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(Notification.class).readValue(change.toString());
	}
}
