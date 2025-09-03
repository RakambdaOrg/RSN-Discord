package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NonNull
	public CommandData getDefinition(@NonNull LocalizationFunction localizationFunction){
		return Commands.slash(getId(), "Bot's info")
				.setLocalizationFunction(localizationFunction)
				.addSubcommands(
						new SubcommandData("general", "Generic information"),
						new SubcommandData("stop", "Stops the bot"),
						new SubcommandData("time", "Current bot's time")
				);
	}
	
	@Override
	@NonNull
	public String getId(){
		return "bot";
	}
}
