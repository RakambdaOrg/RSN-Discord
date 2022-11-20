package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.modal.impl.TimeReactionCreateModal;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TimeReactionCommand implements IRegistrableSlashCommand, IExecutableSlashCommandGuild{
	@Override
	@NotNull
	public String getId(){
		return "time-reaction";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return getId();
	}
	
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.slash("time-reaction", "Time reaction")
				.setLocalizationFunction(localizationFunction)
				.setGuildOnly(true);
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		return JDAWrappers.reply(event, TimeReactionCreateModal.builder().get()).submit();
	}
}
