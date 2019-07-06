package fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class UserConfigurationCommand extends ValueConfigurationCommand<UserConfiguration>{
	public UserConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected UserConfiguration extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		if(event.getMessage().getMentionedUsers().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
	
	@Override
	protected String getValueName(){
		return "User";
	}
}
