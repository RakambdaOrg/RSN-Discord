package fr.raksrinana.rsndiscord.settings.guild.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnilistAccessTokenConfiguration{
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("expireDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime expireDate;
	@JsonProperty("token")
	private String token;
	
	public AnilistAccessTokenConfiguration(){
	}
	
	public AnilistAccessTokenConfiguration(final long userId, @Nonnull final LocalDateTime expireDate, @Nonnull final String token){
		this.userId = userId;
		this.expireDate = expireDate;
		this.token = token;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getToken()).toHashCode();
	}
	
	@Nonnull
	public LocalDateTime getExpireDate(){
		return this.expireDate;
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof AnilistAccessTokenConfiguration)){
			return false;
		}
		final var that = (AnilistAccessTokenConfiguration) o;
		return new EqualsBuilder().append(this.getToken(), that.getToken()).isEquals();
	}
	
	@Nonnull
	public String getToken(){
		return this.token;
	}
	
	public long getUserId(){
		return this.userId;
	}
}
