package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.interaction.InteractionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.NonNull;

public interface IRegistrableSlashCommand extends ISlashCommand{
	@NonNull
	CommandData getDefinition(@NonNull LocalizationFunction localizationFunction);
	
	@NonNull
	default String getRegisterName(){
		return InteractionType.SLASH.prefixed(getId());
	}
	
	default boolean isIncludeAllServers(){
		return false;
	}
}
