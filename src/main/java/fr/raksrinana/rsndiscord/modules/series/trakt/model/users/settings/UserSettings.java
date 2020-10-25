package fr.raksrinana.rsndiscord.modules.series.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class UserSettings{
	@JsonProperty("user")
	private User user;
	@JsonProperty("account")
	private Account account;
	@JsonProperty("connections")
	private Connections connections;
	@JsonProperty("sharing_text")
	private SharingText sharingText;
	
	@Override
	public int hashCode(){
		return Objects.hash(getUser());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		UserSettings that = (UserSettings) o;
		return Objects.equals(getUser(), that.getUser());
	}
}
