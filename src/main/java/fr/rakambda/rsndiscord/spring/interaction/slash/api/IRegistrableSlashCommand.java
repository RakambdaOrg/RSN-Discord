package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

public interface IRegistrableSlashCommand{
	@NotNull
	CommandData getDefinition(@NotNull LocalizationFunction localizationFunction);
}
