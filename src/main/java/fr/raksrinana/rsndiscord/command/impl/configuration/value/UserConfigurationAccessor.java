package fr.raksrinana.rsndiscord.command.impl.configuration.value;

import fr.raksrinana.rsndiscord.command.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class UserConfigurationAccessor extends ValueConfigurationAccessor<UserConfiguration>{
	public UserConfigurationAccessor(String name, Function<GuildConfiguration, Optional<UserConfiguration>> getter, BiConsumer<GuildConfiguration, UserConfiguration> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected UserConfiguration fromString(@NotNull String value){
		return new UserConfiguration(Long.parseLong(value));
	}
}
