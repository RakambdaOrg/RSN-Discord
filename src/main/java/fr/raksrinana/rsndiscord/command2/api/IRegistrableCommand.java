package fr.raksrinana.rsndiscord.command2.api;

import fr.raksrinana.rsndiscord.command2.SlashCommandService;
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
	CommandData getSlashCommand();
	
	@NotNull
	default CompletableFuture<List<CommandPrivilege>> updateCommandPrivileges(@NotNull Guild guild, @NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return SlashCommandService.updateCommandPrivileges(this, guild, commandUpdate);
	}
}
