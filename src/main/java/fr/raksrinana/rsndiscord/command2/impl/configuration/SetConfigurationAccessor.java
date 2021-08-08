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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class SetConfigurationAccessor<T> implements IConfigurationAccessor{
	@Getter
	private final String name;
	private final Function<GuildConfiguration, Set<T>> getter;
	private final BiConsumer<GuildConfiguration, T> setter;
	
	public SetConfigurationAccessor(String name, Function<GuildConfiguration, Set<T>> getter){
		this(name, getter, (s, v) -> getter.apply(s).add(v));
	}
	
	@Override
	public boolean add(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return add(configuration, converted);
	}
	
	private boolean add(GuildConfiguration configuration, T value){
		setter.accept(configuration, value);
		log.info("Added configuration value {} to {}", value, getName());
		return true;
	}
	
	@Override
	public boolean remove(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return remove(configuration, converted);
	}
	
	public boolean remove(@NotNull GuildConfiguration configuration, @Nullable T value){
		getter.apply(configuration).remove(value);
		log.info("Removed configuration value {} from {}", value, getName());
		return true;
	}
	
	@Override
	public boolean reset(@NotNull GuildConfiguration configuration){
		getter.apply(configuration).clear();
		log.info("Cleared configuration {}", getName());
		return true;
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
	
	protected abstract T fromString(@NotNull String value);
}
