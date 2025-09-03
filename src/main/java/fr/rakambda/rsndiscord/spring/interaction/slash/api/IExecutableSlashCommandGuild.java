package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableSlashCommandGuild extends IExecutableSlashCommand{
	@NonNull
	CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws BotException;
	
	@NonNull
	default CompletableFuture<?> autoCompleteGuild(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull Guild guild, @NonNull Member member) throws BotException{
		return CompletableFuture.completedFuture(null);
	}
}
