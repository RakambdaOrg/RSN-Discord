package fr.raksrinana.rsndiscord.command.impl.configuration.value;

import fr.raksrinana.rsndiscord.command.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LongConfigurationAccessor extends ValueConfigurationAccessor<Long>{
	public LongConfigurationAccessor(String name, Function<GuildConfiguration, Optional<Long>> getter, BiConsumer<GuildConfiguration, Long> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected Long fromString(@NotNull String value){
		return Long.parseLong(value);
	}
}
