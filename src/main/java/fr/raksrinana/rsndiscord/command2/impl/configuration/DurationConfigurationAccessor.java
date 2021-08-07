package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DurationConfigurationAccessor extends ValueConfigurationAccessor<Duration>{
	public DurationConfigurationAccessor(Function<GuildConfiguration, Optional<Duration>> getter, BiConsumer<GuildConfiguration, Duration> setter){
		super(getter, setter);
	}
	
	@Override
	protected Duration fromString(@NotNull String value){
		return Duration.ofMillis(Long.parseLong(value));
	}
}
