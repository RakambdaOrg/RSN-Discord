package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.interaction.InteractionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

public interface IRegistrableSlashCommand extends ISlashCommand{
	@NotNull
	CommandData getDefinition(@NotNull LocalizationFunction localizationFunction);
	
	@NotNull
	default String getRegisterName(){
		return InteractionType.SLASH.prefixed(getId());
	}
	
	default boolean isIncludeAllServers(){
		return false;
	}
}
