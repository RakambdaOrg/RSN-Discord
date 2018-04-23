package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import fr.mrcraftcod.gunterdiscord.settings.configs.HangmanChannelConfig;
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
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		List<Role> roles = event.getGuild().getRolesByName("pendu", true);
		if(roles.size() > 0)
		{
			event.getMember().getGuild().getController().addRolesToMember(event.getMember(), roles.get(0)).queue();
			event.getJDA().getTextChannelById(new HangmanChannelConfig().getLong()).sendMessageFormat("%s a rejoint la partie!", event.getAuthor().getAsMention()).queue();
			HangmanListener.setUp(event.getGuild());
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
