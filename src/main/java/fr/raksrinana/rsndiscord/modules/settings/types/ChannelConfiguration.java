package fr.raksrinana.rsndiscord.modules.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.modules.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ChannelConfiguration implements IAtomicConfiguration{
	@JsonProperty("channelId")
	@Setter
	private long channelId;
	
	public ChannelConfiguration(@NonNull final TextChannel channel){
		this(channel.getIdLong());
	}
	
	public ChannelConfiguration(final long channelId){
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
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("channelId", channelId)
				.toString();
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