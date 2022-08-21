package fr.raksrinana.rsndiscord.interaction;

import lombok.Getter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;

public class InteractionService{
	@Getter
	private static final LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction
			.fromBundles("RSNCommands", DiscordLocale.FRENCH)
			.build();
	;
}
