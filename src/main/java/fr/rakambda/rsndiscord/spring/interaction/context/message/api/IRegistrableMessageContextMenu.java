package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.interaction.InteractionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;

public interface IRegistrableMessageContextMenu extends IMessageContextMenu{
	@NotNull
	CommandData getDefinition(@NotNull LocalizationFunction localizationFunction);
	
	@NotNull
	default String getRegisterName(){
		return InteractionType.CONTEXT_MESSAGE.prefixed(getName());
	}
	
	default boolean isIncludeAllServers(){
		return false;
	}
}
