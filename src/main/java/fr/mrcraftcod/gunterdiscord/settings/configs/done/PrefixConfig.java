package fr.mrcraftcod.gunterdiscord.settings.configs.done;

import fr.mrcraftcod.gunterdiscord.settings.configurations.StringValueConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class PrefixConfig extends StringValueConfiguration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public PrefixConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "prefix";
	}
}
