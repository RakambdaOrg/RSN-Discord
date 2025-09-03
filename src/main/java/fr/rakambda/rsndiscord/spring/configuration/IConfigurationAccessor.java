package fr.rakambda.rsndiscord.spring.configuration;

import fr.rakambda.rsndiscord.spring.interaction.exception.OperationNotSupportedException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jspecify.annotations.NonNull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface IConfigurationAccessor{
	default boolean set(@NonNull JDA jda, long guildId, @NonNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.SET);
	}
	
	default boolean add(@NonNull JDA jda, long guildId, @NonNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.ADD);
	}
	
	default boolean remove(@NonNull JDA jda, long guildId, @NonNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.REMOVE);
	}
	
	default boolean reset(@NonNull JDA jda, long guildId) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.RESET);
	}
	
	@NonNull
	default Optional<MessageEmbed> show(long guildId) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.SHOW);
	}
	
	@NonNull
	String getName();
	
	@NonNull
	default Collection<Command.Choice> autoComplete(@NonNull CommandAutoCompleteInteractionEvent event){
		return Set.of();
	}
}
