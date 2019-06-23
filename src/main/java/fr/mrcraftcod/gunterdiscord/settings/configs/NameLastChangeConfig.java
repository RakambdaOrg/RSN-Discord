package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MapConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class NameLastChangeConfig extends MapConfiguration<Long, Long>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public NameLastChangeConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	protected Function<String, Long> getKeyParser(){
		return value -> Objects.isNull(value) ? null : Long.parseLong(value);
	}
	
	@Nonnull
	@Override
	protected Function<String, Long> getValueParser(){
		return value -> Objects.isNull(value) ? null : Long.parseLong(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "nameLastChange";
	}
}
