package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnTimeConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class DoubleWarnCommand extends WarnCommand{
	@Override
	protected Role getRole(final Guild guild) throws NoValueDefinedException{
		return new DoubleWarnRoleConfig(guild).getObject();
	}
	
	@Override
	protected double getTime(final Guild guild){
		return new DoubleWarnTimeConfig(guild).getObject(1D);
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
