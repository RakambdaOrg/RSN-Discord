package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableSlashCommandGuild extends IExecutableSlashCommand{
	@NotNull
	CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws BotException;
	
	@NotNull
	default CompletableFuture<?> autoCompleteGuild(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Guild guild, @NotNull Member member) throws BotException{
		return CompletableFuture.completedFuture(null);
	}
}
