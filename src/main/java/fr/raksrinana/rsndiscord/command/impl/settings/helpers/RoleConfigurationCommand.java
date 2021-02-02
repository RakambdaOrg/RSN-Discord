package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public abstract class RoleConfigurationCommand extends ValueConfigurationCommand<RoleConfiguration>{
	protected RoleConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected RoleConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(event.getMessage().getMentionedRoles().isEmpty()){
			throw new IllegalArgumentException("Please mention the role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Role";
	}
}
