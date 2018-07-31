package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnTimeConfig;
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
public class DoubleWarnCommand extends WarnCommand{
	@Override
	protected SingleRoleConfiguration getRoleConfig(Guild guild){
		return new DoubleWarnRoleConfig(guild);
	}
	
	@Override
	protected DoubleValueConfiguration getTimeConfig(Guild guild){
		return new DoubleWarnTimeConfig(guild);
	}
	
	@Override
	public String getName(){
		return "Double warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("doublewarn", "dwarn");
	}
}
