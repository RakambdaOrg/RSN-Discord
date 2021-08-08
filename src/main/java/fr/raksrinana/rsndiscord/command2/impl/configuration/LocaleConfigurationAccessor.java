package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LocaleConfigurationAccessor extends ValueConfigurationAccessor<Locale>{
	public LocaleConfigurationAccessor(String name, Function<GuildConfiguration, Optional<Locale>> getter, BiConsumer<GuildConfiguration, Locale> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected Locale fromString(@NotNull String value){
		return new Locale(value);
	}
}
