package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

public interface IRegistrableMessageContextMenu {
	@NotNull
	CommandData getDefinition(@NotNull LocalizationFunction localizationFunction);
}
