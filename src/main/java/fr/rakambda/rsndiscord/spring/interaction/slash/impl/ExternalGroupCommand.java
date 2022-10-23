package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.slash("external", "External services")
				.setLocalizationFunction(localizationFunction)
				.addSubcommands(
						new SubcommandData("anilist", "Register your Anilist account"),
						new SubcommandData("trakt", "Register your Trakt account")
				);
	}
}
