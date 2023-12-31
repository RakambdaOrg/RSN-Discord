package fr.rakambda.rsndiscord.spring.configuration;

import fr.rakambda.rsndiscord.spring.interaction.exception.OperationNotSupportedException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IConfigurationAccessor{
	default boolean set(@NotNull JDA jda, long guildId, @NotNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.SET);
	}
	
	default boolean add(@NotNull JDA jda, long guildId, @NotNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.ADD);
	}
	
	default boolean remove(@NotNull JDA jda, long guildId, @NotNull String value) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.REMOVE);
	}
	
	default boolean reset(@NotNull JDA jda, long guildId) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.RESET);
	}
	
	@NotNull
	default Optional<MessageEmbed> show(long guildId) throws OperationNotSupportedException{
		throw new OperationNotSupportedException(ConfigurationOperation.SHOW);
	}
	
	@NotNull
	String getName();
}
