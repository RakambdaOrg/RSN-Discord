package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class HangmanCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		event.getMessage().delete().queue();
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(HangmanListener.resetting)
			return CommandResult.SUCCESS;
		List<Role> roles = event.getGuild().getRolesByName("pendu", true);
		if(roles.size() > 0)
		{
			if(event.getMember().getRoles().contains(roles.get(0)))
				return CommandResult.SUCCESS;
			event.getMember().getGuild().getController().addRolesToMember(event.getMember(), roles.get(0)).queue();
			HangmanListener.setUp(event.getGuild());
			HangmanListener.onPlayerJoin(event.getMember());
		}
		else
			return CommandResult.FAILED;
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Pendu";
	}
	
	@Override
	public String getCommand()
	{
		return "pendu";
	}
}
