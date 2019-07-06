package fr.mrcraftcod.gunterdiscord.settings.configs.done;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MapConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListCodeConfig extends MapConfiguration<Long, String>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public AniListCodeConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	protected Function<String, Long> getKeyParser(){
		return value -> Objects.isNull(value) ? null : Long.parseLong(value);
	}
	
	@Nonnull
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "aniListCode";
	}
}
