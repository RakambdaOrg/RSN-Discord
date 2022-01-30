package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ChannelConfigurationAccessor extends ValueConfigurationAccessor<ChannelConfiguration>{
	public ChannelConfigurationAccessor(String name, Function<GuildConfiguration, Optional<ChannelConfiguration>> getter, BiConsumer<GuildConfiguration, ChannelConfiguration> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected ChannelConfiguration fromString(@NotNull String value){
		return new ChannelConfiguration(Long.parseLong(value));
	}
}
