package fr.raksrinana.rsndiscord.command2.api;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface IRegistrableCommand extends ICommand{
	default boolean getDefaultPermission(){
		return true;
	}
	
	@NotNull
	CommandData getSlashCommand();
	
	@NotNull
	default CompletableFuture<Void> updateGuildCommandPrivileges(@NotNull Guild guild,
			@NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return updateCommandPrivileges(guild, guild.retrieveCommands(), commandUpdate);
	}
	
	@NotNull
	default CompletableFuture<Void> updateGlobalCommandPrivileges(@NotNull Guild guild,
			@NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return updateCommandPrivileges(guild, guild.getJDA().retrieveCommands(), commandUpdate);
	}
	
	@NotNull
	default CompletableFuture<Void> updateCommandPrivileges(@NotNull Guild guild,
			@NotNull RestAction<List<Command>> commandsAction,
			@NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return commandsAction.submit().thenCompose(commands ->
				commands.stream()
						.filter(command -> Objects.equals(command.getName(), getId()))
						.findFirst()
						.map(command -> command.retrievePrivileges(guild).submit()
								.exceptionally(e -> {
									Log.getLogger(guild).error("Failed to retrieve privileges", e);
									return new ArrayList<>();
								})
								.thenApply(commandUpdate)
								.thenCompose(commandPrivileges -> command.updatePrivileges(guild, commandPrivileges).submit()))
						.orElse(CompletableFuture.completedFuture(null))
		);
	}
}
