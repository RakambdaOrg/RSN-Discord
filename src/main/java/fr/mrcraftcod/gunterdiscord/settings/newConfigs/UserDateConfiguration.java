package fr.mrcraftcod.gunterdiscord.settings.newConfigs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampSerializer;
import java.util.Date;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDateConfiguration{
	@JsonProperty("userId")
	private long userId;
	@JsonProperty
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	@JsonSerialize(using = SQLTimestampSerializer.class)
	private Date date;
	
	public Date getDate(){
		return date;
	}
	
	public long getUserId(){
		return userId;
	}
}
