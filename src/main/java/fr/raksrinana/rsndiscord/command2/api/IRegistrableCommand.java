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
	Function<Collection<? extends CommandPrivilege>, ? extends Collection<? extends CommandPrivilege>> DISPLAY_PRIVILEGES = commandPrivileges -> {
		commandPrivileges.forEach(commandPrivilege -> Log.getLogger().info("AFTER PRIVILEGE {} {} {}", commandPrivilege.getType(), commandPrivilege.getId(), commandPrivilege.isEnabled()));
		return commandPrivileges;
	};
	Function<List<CommandPrivilege>, List<CommandPrivilege>> DISPLAY_PRIVILEGES2 = commandPrivileges -> {
		commandPrivileges.forEach(commandPrivilege -> Log.getLogger().info("BEFORE PRIVILEGE {} {} {}", commandPrivilege.getType(), commandPrivilege.getId(), commandPrivilege.isEnabled()));
		return commandPrivileges;
	};
	
	default boolean getDefaultPermission(){
		return true;
	}
	
	default boolean isGuildSpecific(){
		return false;
	}
	
	@NotNull
	CommandData getSlashCommand();
	
	@NotNull
	default CompletableFuture<Void> updateCommandPrivileges(@NotNull Guild guild,
			@NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		if(isGuildSpecific()){
			return updateGuildCommandPrivileges(guild, commandUpdate);
		}
		return updateGlobalCommandPrivileges(guild, commandUpdate);
	}
	
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
								.thenApply(DISPLAY_PRIVILEGES2)
								.thenApply(commandUpdate)
								.thenApply(DISPLAY_PRIVILEGES)
								.thenCompose(commandPrivileges -> command.updatePrivileges(guild, commandPrivileges).submit()))
						.orElse(CompletableFuture.completedFuture(null))
		);
	}
}
