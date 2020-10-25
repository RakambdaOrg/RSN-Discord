package fr.raksrinana.rsndiscord.modules.settings.command.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;

public abstract class ChannelConfigurationCommand extends ValueConfigurationCommand<ChannelConfiguration>{
	protected ChannelConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected ChannelConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
