package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
@SuppressWarnings("WeakerAccess")
public abstract class SingleChannelConfiguration extends ValueConfiguration<TextChannel>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected SingleChannelConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	/**
	 * Tells if this config represents the given channel.
	 *
	 * @param channel The channel.
	 *
	 * @return True if the same channels, false otherwise.
	 */
	public boolean isChannel(@Nullable final TextChannel channel){
		if(Objects.isNull(channel)){
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
	public boolean isChannel(final long ID){
		return getObject().map(channel -> Objects.equals(ID, channel.getIdLong())).orElse(false);
	}
	
	@Nonnull
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> {
			if(event.getMessage().getMentionedChannels().isEmpty()){
				throw new IllegalStateException("Please mention a channel");
			}
			return "" + event.getMessage().getMentionedChannels().get(0).getIdLong();
		};
	}
	
	@Nonnull
	@Override
	protected Function<String, TextChannel> getConfigParser(){
		return Objects.isNull(guild) ? channel -> null : this.guild::getTextChannelById;
	}
	
	@Nonnull
	@Override
	protected Function<TextChannel, String> getValueParser(){
		return channel -> Objects.isNull(channel) ? null : "" + channel.getIdLong();
	}
}
