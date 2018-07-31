package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.configs.MegaWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.MegaWarnTimeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configurations.DoubleValueConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleRoleConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class MegaWarnCommand extends WarnCommand{
	@Override
	protected SingleRoleConfiguration getRoleConfig(Guild guild){
		return new MegaWarnRoleConfig(guild);
	}
	
	@Override
	protected DoubleValueConfiguration getTimeConfig(Guild guild){
		return new MegaWarnTimeConfig(guild);
	}
	
	@Override
	public String getName(){
		return "Mega warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("megawarn", "mwarn");
	}
}
