package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.configs.WarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.WarnTimeConfig;
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
public class NormalWarnCommand extends WarnCommand{
	@Override
	protected SingleRoleConfiguration getRoleConfig(Guild guild){
		return new WarnRoleConfig(guild);
	}
	
	@Override
	protected DoubleValueConfiguration getTimeConfig(Guild guild){
		return new WarnTimeConfig(guild);
	}
	
	@Override
	public String getName(){
		return "Warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("warn");
	}
}
