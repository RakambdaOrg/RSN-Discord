package fr.raksrinana.rsndiscord.interaction.command.api;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface IRegistrableCommand extends ICommand{
	default DefaultMemberPermissions getDefaultPermission(){
		return DefaultMemberPermissions.ENABLED;
	}
	
	@NotNull
	CommandData getDefinition();
	
	default boolean isGuildOnly(){
		return false;
	}
}
