package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class ValueConfigurationAccessor<T> implements IConfigurationAccessor{
	@Getter
	private final String name;
	private final Function<GuildConfiguration, Optional<T>> getter;
	private final BiConsumer<GuildConfiguration, T> setter;
	
	@Override
	public boolean set(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return set(configuration, converted);
	}
	
	private boolean set(@NotNull GuildConfiguration configuration, @Nullable T value){
		setter.accept(configuration, value);
		log.info("Set configuration {} to {}", getName(), value);
		return true;
	}
	
	@Override
	public boolean reset(@NotNull GuildConfiguration configuration){
		return set(configuration, getResetValue());
	}
	
	@Override
	@NotNull
	public Optional<MessageEmbed> show(@NotNull GuildConfiguration configuration){
		var value = getter.apply(configuration);
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value.toString(), false);
		return Optional.of(builder.build());
	}
	
	@Nullable
	private T getResetValue(){
		return null;
	}
	
	@Nullable
	protected abstract T fromString(@NotNull String value);
}
