package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ChannelSetConfigurationAccessor extends SetConfigurationAccessor<ChannelConfiguration>{
	public ChannelSetConfigurationAccessor(Function<GuildConfiguration, Set<ChannelConfiguration>> getter){
		super(getter);
	}
	
	public ChannelSetConfigurationAccessor(Function<GuildConfiguration, Set<ChannelConfiguration>> getter, BiConsumer<GuildConfiguration, ChannelConfiguration> setter){
		super(getter, setter);
	}
	
	@Override
	protected ChannelConfiguration fromString(@NotNull String value){
		return new ChannelConfiguration(Long.parseLong(value));
	}
}
