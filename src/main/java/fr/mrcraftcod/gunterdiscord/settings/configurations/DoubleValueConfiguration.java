package fr.mrcraftcod.gunterdiscord.settings.configurations;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class DoubleValueConfiguration extends ValueConfiguration<Double>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected DoubleValueConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	protected BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser(){
		return (event, arg) -> arg;
	}
	
	@Nonnull
	@Override
	protected Function<String, Double> getConfigParser(){
		return value -> Objects.isNull(value) ? null : Double.parseDouble(value);
	}
	
	@Nonnull
	@Override
	protected Function<Double, String> getValueParser(){
		return value -> Objects.isNull(value) ? null : value.toString();
	}
}
