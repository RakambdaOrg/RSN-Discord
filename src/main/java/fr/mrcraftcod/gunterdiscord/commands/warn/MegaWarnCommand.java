package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.configs.MegaWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.MegaWarnTimeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleRoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configurations.ValueConfiguration;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class MegaWarnCommand extends WarnCommand{
	@Override
	public String getName(){
		return "Mega warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("megawarn", "mwarn");
	}
	
	@Override
	protected SingleRoleConfiguration getRoleConfig(){
		return new MegaWarnRoleConfig();
	}
	
	@Override
	protected ValueConfiguration getTimeConfig(){
		return new MegaWarnTimeConfig();
	}
}
