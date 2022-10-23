package fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connections{
	private boolean facebook;
	private boolean twitter;
	private boolean google;
	private boolean tumblr;
	private boolean medium;
	private boolean slack;
}
