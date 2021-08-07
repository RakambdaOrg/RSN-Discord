package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.api.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ChannelConfiguration implements IAtomicConfiguration{
	@JsonProperty("channelId")
	@Setter
	private long channelId;
	
	public ChannelConfiguration(@NotNull TextChannel channel){
		this(channel.getIdLong());
	}
	
	public ChannelConfiguration(long channelId){
		this.channelId = channelId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getChannelId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof ChannelConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getChannelId(), that.getChannelId()).isEquals();
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
	
	@NotNull
	public Optional<TextChannel> getChannel(){
		return ofNullable(Main.getJda().getTextChannelById(getChannelId()));
	}
	
	public void setChannel(@NotNull TextChannel channel){
		setChannelId(channel.getIdLong());
	}
}
