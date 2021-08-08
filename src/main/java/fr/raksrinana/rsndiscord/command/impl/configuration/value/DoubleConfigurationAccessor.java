package fr.raksrinana.rsndiscord.command.impl.configuration.value;

import fr.raksrinana.rsndiscord.command.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DoubleConfigurationAccessor extends ValueConfigurationAccessor<Double>{
	public DoubleConfigurationAccessor(String name, Function<GuildConfiguration, Optional<Double>> getter, BiConsumer<GuildConfiguration, Double> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected Double fromString(@NotNull String value){
		return Double.parseDouble(value);
	}
}
