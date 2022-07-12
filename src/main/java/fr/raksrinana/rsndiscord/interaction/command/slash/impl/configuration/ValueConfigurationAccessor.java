package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Log4j2
public abstract class ValueConfigurationAccessor<T> extends BaseConfigurationAccessor{
	private final Function<GuildConfiguration, Optional<T>> getter;
	private final BiConsumer<GuildConfiguration, T> setter;
	
	public ValueConfigurationAccessor(@NotNull String name, Function<GuildConfiguration, Optional<T>> getter, BiConsumer<GuildConfiguration, T> setter){
		super(name);
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public boolean set(@NotNull GuildConfiguration configuration, @NotNull String value){
		var converted = fromString(value);
		return setType(configuration, converted);
	}
	
	private boolean setType(@NotNull GuildConfiguration configuration, @Nullable T value){
		setter.accept(configuration, value);
		log.info("Set configuration {} to {}", getName(), value);
		return true;
	}
	
	@Override
	public boolean reset(@NotNull GuildConfiguration configuration){
		return setType(configuration, getResetValue());
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
