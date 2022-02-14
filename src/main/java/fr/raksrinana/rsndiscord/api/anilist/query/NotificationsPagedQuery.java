package fr.raksrinana.rsndiscord.api.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.api.anilist.data.notifications.Notification;
import kong.unirest.core.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.AIRING;
import static fr.raksrinana.rsndiscord.api.anilist.data.notifications.NotificationType.RELATED_MEDIA_ADDITION;

public class NotificationsPagedQuery implements IPagedQuery<Notification>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $type_in: [NotificationType]", Notification.getQuery());
	
	private final JSONObject variables;
	private int currentPage = 0;
	
	public NotificationsPagedQuery(){
		variables = new JSONObject();
		variables.put("page", 1);
		variables.put("perPage", PER_PAGE);
		variables.put("type_in", List.of(AIRING.name(), RELATED_MEDIA_ADDITION.name()));
	}
	
	@NotNull
	@Override
	public String getQuery(){
		return QUERY;
	}
	
	@NotNull
	@Override
	public JSONObject getParameters(int page){
		variables.put("page", page);
		return variables;
	}
	
	@Override
	public boolean isFetchNextPage(){
		return currentPage < 1;
	}
	
	@Override
	public int getNextPage(){
		return ++currentPage;
	}
	
	@NotNull
	@Override
	public String getPageElementName(){
		return "notifications";
	}
	
	@Nullable
	public Notification buildChange(@NotNull JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(Notification.class).readValue(change.toString());
	}
}
