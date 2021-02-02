package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public abstract class ChannelConfigurationCommand extends ValueConfigurationCommand<ChannelConfiguration>{
	protected ChannelConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected ChannelConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(event.getMessage().getMentionedChannels().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new ChannelConfiguration(event.getMessage().getMentionedChannels().get(0));
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Channel";
	}
}
