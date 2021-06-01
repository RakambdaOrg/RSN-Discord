package fr.raksrinana.rsndiscord.settings.general.trakt;

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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class TraktAccessTokenConfiguration implements IAtomicConfiguration{
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
	
	public TraktAccessTokenConfiguration(long userId, @NotNull ZonedDateTime expireDate, @NotNull String token, @NotNull String refreshToken){
		this.userId = userId;
		this.expireDate = expireDate;
		this.token = token;
		this.refreshToken = refreshToken;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getToken()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof TraktAccessTokenConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getToken(), that.getToken()).isEquals();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return false;
	}
	
	public boolean isExpired(){
		return ZonedDateTime.now().minusMinutes(1).isAfter(getExpireDate());
	}
}
