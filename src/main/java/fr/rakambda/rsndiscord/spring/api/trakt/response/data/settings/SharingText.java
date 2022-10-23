package fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharingText{
	private String watching;
	private String watched;
	private String rated;
}
