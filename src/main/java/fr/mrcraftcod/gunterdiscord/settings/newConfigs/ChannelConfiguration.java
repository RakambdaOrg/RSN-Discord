package fr.mrcraftcod.gunterdiscord.settings.newConfigs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.api.entities.TextChannel;
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
	
	public ChannelConfiguration(@Nonnull TextChannel channel){
		this(channel.getIdLong());
	}
	
	public ChannelConfiguration(long channelId){
		this.channelId = channelId;
	}
	
	@Nonnull
	public Optional<TextChannel> getChannel(){
		return Optional.ofNullable(Main.getJDA().getTextChannelById(channelId));
	}
	
	public long getChannelId(){
		return channelId;
	}
}
