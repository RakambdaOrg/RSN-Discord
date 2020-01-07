package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.net.URL;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AmazonTrackingConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("channel")
	private ChannelConfiguration notificationChannel;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	@JsonSerialize(using = URLSerializer.class)
	private URL url;
	@JsonProperty("lastPrice")
	@Setter
	private double lastPrice;
	
	public AmazonTrackingConfiguration(@NonNull final User user, @NonNull URL url, @NonNull TextChannel channel){
		this(new UserConfiguration(user), url, new ChannelConfiguration(channel), -1);
	}
	
	public AmazonTrackingConfiguration(@NonNull UserConfiguration user, @NonNull URL url, @NonNull ChannelConfiguration channel, double price){
		this.user = user;
		this.url = url;
		this.notificationChannel = channel;
		this.lastPrice = price;
	}
	
	public AmazonTrackingConfiguration(@NonNull final User user, @NonNull URL url, @NonNull TextChannel channel, double price){
		this(new UserConfiguration(user), url, new ChannelConfiguration(channel), price);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getUser(), getUrl());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof AmazonTrackingConfiguration)){
			return false;
		}
		AmazonTrackingConfiguration that = (AmazonTrackingConfiguration) o;
		return Objects.equals(getUser(), that.getUser()) && Objects.equals(getUrl(), that.getUrl());
	}
}
