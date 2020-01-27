package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserDateConfiguration implements AtomicConfiguration{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	@JsonProperty("userId")
	private UserConfiguration user;
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Setter
	private LocalDateTime date;
	
	public UserDateConfiguration(@NonNull final User user, @NonNull final LocalDateTime date){
		this(new UserConfiguration(user.getIdLong()), date);
	}
	
	private UserDateConfiguration(@NonNull final UserConfiguration user, @NonNull final LocalDateTime date){
		this.user = user;
		this.date = date;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getUser()).append(this.getDate()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserDateConfiguration)){
			return false;
		}
		final var that = (UserDateConfiguration) o;
		return new EqualsBuilder().append(this.getUser(), that.getUser()).append(this.getDate(), that.getDate()).isEquals();
	}
	
	@Override
	public String toString(){
		return "UserDate(" + this.getUser().toString() + '|' + this.getDate().format(DF) + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved();
	}
}
