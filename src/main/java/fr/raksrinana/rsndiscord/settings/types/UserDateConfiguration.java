package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserDateConfiguration implements IAtomicConfiguration{
	@JsonProperty("userId")
	private UserConfiguration user;
	@JsonProperty("date")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@Setter
	private ZonedDateTime date;
	
	public UserDateConfiguration(@NotNull User user, @NotNull ZonedDateTime date){
		this(new UserConfiguration(user.getIdLong()), date);
	}
	
	private UserDateConfiguration(@NotNull UserConfiguration user, @NotNull ZonedDateTime date){
		this.user = user;
		this.date = date;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getUser()).append(getDate()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserDateConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getUser(), that.getUser()).append(getDate(), that.getDate()).isEquals();
	}
	
	@Override
	public String toString(){
		return "UserDate(" + getUser().toString() + '|' + getDate() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved();
	}
}
