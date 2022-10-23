package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableSlashCommandUser extends IExecutableSlashCommand{
	@NotNull
	CompletableFuture<?> executeUser(@NotNull SlashCommandInteraction event) throws BotException;
	
	@NotNull
	default CompletableFuture<?> autoCompleteUser(@NotNull CommandAutoCompleteInteractionEvent event) throws BotException{
		return CompletableFuture.completedFuture(null);
	}
}
