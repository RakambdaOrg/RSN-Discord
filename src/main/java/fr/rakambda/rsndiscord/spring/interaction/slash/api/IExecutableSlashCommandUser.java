package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableSlashCommandUser extends IExecutableSlashCommand{
	@NonNull
	CompletableFuture<?> executeUser(@NonNull SlashCommandInteraction event) throws BotException;
	
	@NonNull
	default CompletableFuture<?> autoCompleteUser(@NonNull CommandAutoCompleteInteractionEvent event) throws BotException{
		return CompletableFuture.completedFuture(null);
	}
}
