package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MultipleUserConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-28.
 *
 * @author Thomas Couchoud
 * @since 2018-10-28
 */
public class EmotesParticipationPinConfig extends MultipleUserConfiguration{
	public EmotesParticipationPinConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "emotesParticipationPin";
	}
}
