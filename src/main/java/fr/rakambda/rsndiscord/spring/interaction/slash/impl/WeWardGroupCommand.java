package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward.AddCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Configuration
public class WeWardGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NonNull
	public CommandData getDefinition(@NonNull LocalizationFunction localizationFunction){
		return Commands.slash(getId(), "WaWard")
				.setLocalizationFunction(localizationFunction)
				.addSubcommands(
						new SubcommandData("add", "Add a card")
								.addOptions(
										new OptionData(STRING, AddCommand.PACK_OPTION_ID, "Pack name")
												.setAutoComplete(true)
												.setRequired(true),
										new OptionData(STRING, AddCommand.NAME_OPTION_ID, "Card name")
												.setAutoComplete(true)
												.setRequired(true)
								)
				);
	}
	
	@Override
	@NonNull
	public String getId(){
		return "weward";
	}
}
