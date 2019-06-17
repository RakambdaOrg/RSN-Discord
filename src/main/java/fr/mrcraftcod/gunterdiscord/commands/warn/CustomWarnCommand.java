package fr.mrcraftcod.gunterdiscord.commands.warn;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class CustomWarnCommand extends WarnCommand{
	@Override
	protected Role getRole(final Guild guild, final Message message, final LinkedList<String> args){
		args.pop();
		return message.getMentionedRoles().get(0);
	}
	
	@Override
	protected double getTime(final Guild guild, final Message message, final LinkedList<String> args){
		return Double.parseDouble(Objects.requireNonNull(args.poll()));
	}
	
	@Override
	public String getName(){
		return "Custom warn";
	}
	
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public List<String> getCommandStrings(){
		return List.of("cwarn");
	}
}
