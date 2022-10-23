package fr.rakambda.rsndiscord.spring.api.anilist.response;

import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class NotificationPagedResponse extends PagedResponse{
	private List<Notification> notifications = new LinkedList<>();
}
