package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
public abstract class SingleChannelConfiguration extends ValueConfiguration<TextChannel>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected SingleChannelConfiguration(Guild guild){
		super(guild);
	}
	
	/**
	 * Tells if this config represents the given channel.
	 *
	 * @param channel The channel.
	 *
	 * @return True if the same channels, false otherwise.
	 */
	public boolean isChannel(TextChannel channel){
		if(channel == null){
			return false;
		}
		return isChannel(channel.getIdLong());
	}
	
	/**
	 * Tells if this config represents the given channel.
	 *
	 * @param ID The ID of the channel.
	 *
	 * @return True if the same channels, false otherwise.
	 */
	public boolean isChannel(long ID){
		try{
			TextChannel channel = getObject();
			if(channel == null){
				return false;
			}
			return ID == channel.getIdLong();
		}
		catch(NoValueDefinedException e){
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected BiFunction<MessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedChannels().isEmpty()){
				throw new IllegalStateException("Please mention a channel");
			}
			return "" + event.getMessage().getMentionedChannels().get(0).getIdLong();
		};
	}
	
	@Override
	protected Function<String, TextChannel> getConfigParser(){
		return guild::getTextChannelById;
	}
	
	@Override
	protected Function<TextChannel, String> getValueParser(){
		return channel -> "" + channel.getIdLong();
	}
}
