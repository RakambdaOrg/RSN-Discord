package fr.raksrinana.rsndiscord.interaction.command.api;

import fr.raksrinana.rsndiscord.interaction.command.CommandService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface IRegistrableCommand extends ICommand{
	default boolean getDefaultPermission(){
		return true;
	}
	
	default boolean isGuildSpecific(){
		return false;
	}
	
	@NotNull
	CommandData getDefinition();
	
	@NotNull
	default CompletableFuture<List<CommandPrivilege>> updateCommandPrivileges(@NotNull Guild guild, @NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return CommandService.updateCommandPrivileges(this, guild, commandUpdate);
	}
	
	default boolean isGuildOnly(){
		return false;
	}
}
