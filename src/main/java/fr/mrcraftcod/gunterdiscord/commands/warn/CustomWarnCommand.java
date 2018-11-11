package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class CustomWarnCommand extends WarnCommand{
	@Override
	protected Role getRole(final Guild guild, final Message message, final LinkedList<String> args) throws NoValueDefinedException{
		args.pop();
		return message.getMentionedRoles().get(0);
	}
	
	@Override
	protected double getTime(final Guild guild, final Message message, final LinkedList<String> args){
		return Double.parseDouble(args.poll());
	}
	
	@Override
	public String getName(){
		return "Custom warn";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("cwarn");
	}
}
