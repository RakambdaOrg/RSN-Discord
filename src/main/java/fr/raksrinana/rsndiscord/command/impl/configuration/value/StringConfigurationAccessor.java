package fr.raksrinana.rsndiscord.command.impl.configuration.value;

import fr.raksrinana.rsndiscord.command.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StringConfigurationAccessor extends ValueConfigurationAccessor<String>{
	public StringConfigurationAccessor(String name, Function<GuildConfiguration, Optional<String>> getter, BiConsumer<GuildConfiguration, String> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected String fromString(@NotNull String value){
		return value;
	}
}
