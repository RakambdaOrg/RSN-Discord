package fr.rakambda.rsndiscord.spring.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraktSettings{
	private String clientId;
	private String clientSecret;
}
