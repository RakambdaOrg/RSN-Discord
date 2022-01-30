package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.SetConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RoleSetConfigurationAccessor extends SetConfigurationAccessor<RoleConfiguration>{
	public RoleSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<RoleConfiguration>> getter){
		super(name, getter);
	}
	
	public RoleSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<RoleConfiguration>> getter, BiConsumer<GuildConfiguration, RoleConfiguration> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected RoleConfiguration fromString(@NotNull String value){
		return new RoleConfiguration(Long.parseLong(value));
	}
}
