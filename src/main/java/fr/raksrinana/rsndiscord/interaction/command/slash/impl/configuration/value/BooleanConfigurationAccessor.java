package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooleanConfigurationAccessor extends ValueConfigurationAccessor<Boolean>{
	public BooleanConfigurationAccessor(String name, Function<GuildConfiguration, Optional<Boolean>> getter, BiConsumer<GuildConfiguration, Boolean> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected Boolean fromString(@NotNull String value){
		return Boolean.parseBoolean(value);
	}
}
