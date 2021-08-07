package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class SetConfigurationAccessor<T> implements IConfigurationAccessor{
	private final Function<GuildConfiguration, Set<T>> getter;
	private final BiConsumer<GuildConfiguration, T> setter;
	
	public SetConfigurationAccessor(Function<GuildConfiguration, Set<T>> getter){
		this(getter, (s, v) -> getter.apply(s).add(v));
	}
	
	public SetConfigurationAccessor(Function<GuildConfiguration, Set<T>> getter, BiConsumer<GuildConfiguration, T> setter){
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public boolean add(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return add(configuration, converted);
	}
	
	private boolean add(GuildConfiguration configuration, T value){
		setter.accept(configuration, value);
		return true;
	}
	
	@Override
	public boolean remove(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return remove(configuration, converted);
	}
	
	public boolean remove(@NotNull GuildConfiguration configuration, @Nullable T value){
		getter.apply(configuration).remove(value);
		return true;
	}
	
	@Override
	public boolean reset(@NotNull GuildConfiguration configuration){
		getter.apply(configuration).clear();
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
