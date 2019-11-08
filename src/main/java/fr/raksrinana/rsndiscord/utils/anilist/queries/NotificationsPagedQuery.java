package fr.raksrinana.rsndiscord.utils.anilist.queries;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.Notification;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.NotificationType;
import kong.unirest.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class NotificationsPagedQuery implements PagedQuery<Notification>{
	private static final String QUERY_NOTIFICATIONS = PagedQuery.pagedQuery("$type_in: [NotificationType]", Notification.getQuery());
	private final JSONObject variables;
	private final LocalDateTime date;
	private int nextPage = 0;
	
	public NotificationsPagedQuery(final int userId, final LocalDateTime date){
		this.date = date;
		this.variables = new JSONObject();
		this.variables.put("page", 1);
		this.variables.put("perPage", PER_PAGE);
		this.variables.put("type_in", List.of(NotificationType.AIRING.name(), NotificationType.RELATED_MEDIA_ADDITION.name()));
	}
	
	@Nonnull
	@Override
	public String getQuery(){
		return QUERY_NOTIFICATIONS;
	}
	
	@Nonnull
	@Override
	public JSONObject getParameters(final int page){
		this.variables.put("page", page);
		return this.variables;
	}
	
	@Override
	public int getNextPage(){
		return ++this.nextPage;
	}
	
	@Nonnull
	@Override
	public String getPageElementName(){
		return "notifications";
	}
	
	@Nonnull
	public Notification buildChange(@Nonnull final JSONObject change) throws Exception{
		return new ObjectMapper().readerFor(Notification.class).readValue(change.toString());
	}
	
	@Nullable
	@Override
	public LocalDateTime getBaseDate(){
		return this.date;
	}
}
