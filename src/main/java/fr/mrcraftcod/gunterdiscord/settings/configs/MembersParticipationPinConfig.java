package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MultipleUserConfiguration;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-28.
 *
 * @author Thomas Couchoud
 * @since 2018-10-28
 */
public class MembersParticipationPinConfig extends MultipleUserConfiguration{
	public MembersParticipationPinConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public String getName(){
		return "membersParticipationPin";
	}
}
