package fr.mrcraftcod.gunterdiscord.newSettings.guild.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampSerializer;
import javax.annotation.Nonnull;
import java.util.Date;

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
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	@JsonSerialize(using = SQLTimestampSerializer.class)
	private Date expireDate;
	@JsonProperty("token")
	private String token;
	
	public AnilistAccessTokenConfiguration(){
	}
	
	public AnilistAccessTokenConfiguration(final long userId, @Nonnull final Date expireDate, @Nonnull final String token){
		this.userId = userId;
		this.expireDate = expireDate;
		this.token = token;
	}
	
	@Nonnull
	public Date getExpireDate(){
		return this.expireDate;
	}
	
	@Nonnull
	public String getToken(){
		return this.token;
	}
	
	public long getUserId(){
		return this.userId;
	}
}
