package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.set;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.SetConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StringSetConfigurationAccessor extends SetConfigurationAccessor<String>{
	public StringSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<String>> getter){
		super(name, getter);
	}
	
	public StringSetConfigurationAccessor(String name, Function<GuildConfiguration, Set<String>> getter, BiConsumer<GuildConfiguration, String> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected String fromString(@NotNull String value){
		return value;
	}
}
