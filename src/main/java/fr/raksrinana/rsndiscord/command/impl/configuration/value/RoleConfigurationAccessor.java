package fr.raksrinana.rsndiscord.command.impl.configuration.value;

import fr.raksrinana.rsndiscord.command.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RoleConfigurationAccessor extends ValueConfigurationAccessor<RoleConfiguration>{
	public RoleConfigurationAccessor(String name, Function<GuildConfiguration, Optional<RoleConfiguration>> getter, BiConsumer<GuildConfiguration, RoleConfiguration> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected RoleConfiguration fromString(@NotNull String value){
		return new RoleConfiguration(Long.parseLong(value));
	}
}
