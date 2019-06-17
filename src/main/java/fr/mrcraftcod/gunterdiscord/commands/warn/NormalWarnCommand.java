package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.WarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.WarnTimeConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class NormalWarnCommand extends WarnCommand{
	@Override
	protected Role getRole(final Guild guild, final Message message, final LinkedList<String> args) throws NoValueDefinedException{
		return new WarnRoleConfig(guild).getObject();
	}
	
	@Override
	protected double getTime(final Guild guild, final Message message, final LinkedList<String> args){
		return new WarnTimeConfig(guild).getObject(1D);
	}
	
	@Override
	public String getName(){
		return "Warn";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("warn");
	}
}
