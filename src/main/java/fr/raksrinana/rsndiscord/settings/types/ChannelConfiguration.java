package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ChannelConfiguration implements AtomicConfiguration{
	@JsonProperty("channelId")
	@Setter
	private long channelId;
	
	public ChannelConfiguration(@NonNull final TextChannel channel){
		this(channel.getIdLong());
	}
	
	ChannelConfiguration(final long channelId){
		this.channelId = channelId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getChannelId()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof ChannelConfiguration)){
			return false;
		}
		final var that = (ChannelConfiguration) o;
		return new EqualsBuilder().append(this.getChannelId(), that.getChannelId()).isEquals();
	}
	
	@Override
	public String toString(){
		return "Channel(" + this.getChannelId() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getChannel().isEmpty();
	}
	
	@NonNull
	public Optional<TextChannel> getChannel(){
		return Optional.ofNullable(Main.getJda().getTextChannelById(this.getChannelId()));
	}
	
	public void setChannel(@NonNull final TextChannel channel){
		this.setChannelId(channel.getIdLong());
	}
}
