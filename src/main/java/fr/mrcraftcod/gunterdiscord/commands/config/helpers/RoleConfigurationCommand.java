package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class RoleConfigurationCommand extends ValueConfigurationCommand<RoleConfiguration>{
	protected RoleConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	protected RoleConfiguration extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(event.getMessage().getMentionedRoles().isEmpty()){
			throw new IllegalArgumentException("Please mention the role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
	}
	
	@Override
	protected String getValueName(){
		return "Role";
	}
}
