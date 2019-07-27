package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelConfiguration{
	@JsonProperty("channelId")
	private long channelId;
	
	public ChannelConfiguration(){
	}
	
	public ChannelConfiguration(@Nonnull final TextChannel channel){
		this(channel.getIdLong());
	}
	
	private ChannelConfiguration(final long channelId){
		this.channelId = channelId;
	}
	
	@Override
	public String toString(){
		return this.getChannel().map(TextChannel::getAsMention).orElse("");
	}
	
	@Nonnull
	public Optional<TextChannel> getChannel(){
		return Optional.ofNullable(Main.getJDA().getTextChannelById(this.getChannelId()));
	}
	
	public void setChannel(@Nonnull TextChannel channel){
		this.setChannel(channel.getIdLong());
	}
	
	private void setChannel(long channelId){
		this.channelId = channelId;
	}
	
	public long getChannelId(){
		return this.channelId;
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
		if(!(o instanceof ChannelConfiguration)){
			return false;
		}
		ChannelConfiguration that = (ChannelConfiguration) o;
		return new EqualsBuilder().append(getChannelId(), that.getChannelId()).isEquals();
	}
}