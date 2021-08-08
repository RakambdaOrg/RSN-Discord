package fr.raksrinana.rsndiscord.command.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IConfigurationAccessor{
	default boolean set(@NotNull GuildConfiguration configuration, @NotNull String value){
		throw new UnsupportedOperationException("Set operation not available");
	}
	
	default boolean add(@NotNull GuildConfiguration configuration, @NotNull String value){
		throw new UnsupportedOperationException("Add operation not available");
	}
	
	default boolean remove(@NotNull GuildConfiguration configuration, @NotNull String value){
		throw new UnsupportedOperationException("Remove operation not available");
	}
	
	default boolean reset(@NotNull GuildConfiguration configuration){
		throw new UnsupportedOperationException("Reset operation not available");
	}
	
	@NotNull
	default Optional<MessageEmbed> show(@NotNull GuildConfiguration configuration){
		throw new UnsupportedOperationException("Show operation not available");
	}
	
	@NotNull
	String getName();
}
