package fr.raksrinana.rsndiscord.command.impl.configuration.map;

import fr.raksrinana.rsndiscord.command.impl.configuration.MapConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.function.Function;

public class ChannelMapConfigurationAccessor extends MapConfigurationAccessor<ChannelConfiguration, ChannelConfiguration>{
	public ChannelMapConfigurationAccessor(String name, Function<GuildConfiguration, Map<ChannelConfiguration, ChannelConfiguration>> getter, TriConsumer<GuildConfiguration, ChannelConfiguration, ChannelConfiguration> setter){
		super(name, getter, setter);
	}
	
	public ChannelMapConfigurationAccessor(@NotNull String name, @NotNull Function<GuildConfiguration, Map<ChannelConfiguration, ChannelConfiguration>> getter){
		super(name, getter);
	}
	
	@Override
	@NotNull
	protected ChannelConfiguration keyFromString(@NotNull String value){
		var parts = value.split(";", 2);
		return new ChannelConfiguration(Long.parseLong(parts[0]));
	}
	
	@Override
	@Nullable
	protected ChannelConfiguration valueFromString(@NotNull String value){
		var parts = value.split(";", 2);
		return parts.length < 2 ? null : new ChannelConfiguration(Long.parseLong(parts[1]));
	}
}
