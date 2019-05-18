package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleChannelConfiguration extends ListConfiguration<TextChannel>{
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MultipleChannelConfiguration(final Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<TextChannel, String> getValueParser(){
		return channel -> "" + channel.getIdLong();
	}
	
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
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
}
