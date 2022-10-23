package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airing{
	private String day;
	private String time;
	private String timezone;
}
