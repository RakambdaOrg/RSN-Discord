package fr.raksrinana.rsndiscord.modules.anilist.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.modules.anilist.data.notifications.INotification;
import kong.unirest.json.JSONObject;
import lombok.NonNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.anilist.data.notifications.NotificationType.AIRING;
import static fr.raksrinana.rsndiscord.modules.anilist.data.notifications.NotificationType.RELATED_MEDIA_ADDITION;

public class NotificationsPagedQuery implements IPagedQuery<INotification>{
	private static final String QUERY = IPagedQuery.pagedQuery(", $type_in: [NotificationType]", INotification.getQuery());
	private final JSONObject variables;
	private int currentPage = 0;
	
	public NotificationsPagedQuery(){
		this.variables = new JSONObject();
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		this.variables.put("type_in", List.of(AIRING.name(), RELATED_MEDIA_ADDITION.name()));
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
	public boolean isFetchNextPage(){
		return currentPage < 1;
	}
	
	@NonNull
	@Override
	public String getPageElementName(){
		return "notifications";
	}
	
	@NonNull
	public INotification buildChange(@NonNull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(INotification.class).readValue(change.toString());
	}
	
	@Override
	public int getNextPage(){
		return ++this.currentPage;
	}
}
