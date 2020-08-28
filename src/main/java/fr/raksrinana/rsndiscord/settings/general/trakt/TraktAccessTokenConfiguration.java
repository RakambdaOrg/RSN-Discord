package fr.raksrinana.rsndiscord.settings.general.trakt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class TraktAccessTokenConfiguration implements AtomicConfiguration{
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("expireDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime expireDate;
	@JsonProperty("token")
	private String token;
	@JsonProperty("refreshToken")
	private String refreshToken;
	
	public TraktAccessTokenConfiguration(final long userId, @NonNull final ZonedDateTime expireDate, @NonNull final String token, @NonNull final String refreshToken){
		this.userId = userId;
		this.expireDate = expireDate;
		this.token = token;
		this.refreshToken = refreshToken;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getToken()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof TraktAccessTokenConfiguration)){
			return false;
		}
		final var that = (TraktAccessTokenConfiguration) o;
		return new EqualsBuilder().append(this.getToken(), that.getToken()).isEquals();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return false;
	}
	
	public boolean isExpired(){
		return ZonedDateTime.now().minusMinutes(1).isAfter(this.getExpireDate());
	}
}
