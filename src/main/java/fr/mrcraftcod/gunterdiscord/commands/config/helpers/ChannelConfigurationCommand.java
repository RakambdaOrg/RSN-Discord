package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class ChannelConfigurationCommand extends ValueConfigurationCommand<ChannelConfiguration>{
	protected ChannelConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	protected ChannelConfiguration extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@Override
	protected String getValueName(){
		return "Channel";
	}
}
