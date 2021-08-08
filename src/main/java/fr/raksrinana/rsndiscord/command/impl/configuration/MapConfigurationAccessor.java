package fr.raksrinana.rsndiscord.command.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public abstract class MapConfigurationAccessor<K, V> implements IConfigurationAccessor{
	@Getter
	private final String name;
	private final Function<GuildConfiguration, Map<K, V>> getter;
	private final TriConsumer<GuildConfiguration, K, V> setter;
	
	public MapConfigurationAccessor(@NotNull String name, @NotNull Function<GuildConfiguration, Map<K, V>> getter){
		this(name, getter, (s, k, v) -> getter.apply(s).put(k, v));
	}
	
	@Override
	public boolean add(@NotNull GuildConfiguration configuration, @NotNull String value){
		return addType(configuration, keyFromString(value), valueFromString(value));
	}
	
	private boolean addType(@NotNull GuildConfiguration configuration, @NotNull K key, @Nullable V value){
		setter.accept(configuration, key, value);
		log.info("Added configuration value {}={} to {}", key, value, getName());
		return true;
	}
	
	@Override
	public boolean remove(@NotNull GuildConfiguration configuration, @NotNull String value){
		return removeType(configuration, keyFromString(value));
	}
	
	public boolean removeType(@NotNull GuildConfiguration configuration, @NotNull K key){
		getter.apply(configuration).remove(key);
		log.info("Removed configuration value {} from {}", key, getName());
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
	
	@NotNull
	protected abstract K keyFromString(@NotNull String value);
	
	@Nullable
	protected abstract V valueFromString(@NotNull String value);
}
