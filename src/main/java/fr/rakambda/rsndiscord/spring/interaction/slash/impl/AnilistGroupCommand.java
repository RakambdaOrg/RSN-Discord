package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnilistGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NonNull
	public CommandData getDefinition(@NonNull LocalizationFunction localizationFunction){
		return Commands.slash(getId(), "Anilist")
				.setLocalizationFunction(localizationFunction)
				.addSubcommands(
						new SubcommandData("register", "Register an account"),
						new SubcommandData("unregister", "Unregister an account")
				);
	}
	
	@Override
	@NonNull
	public String getId(){
		return "anilist";
	}
}
