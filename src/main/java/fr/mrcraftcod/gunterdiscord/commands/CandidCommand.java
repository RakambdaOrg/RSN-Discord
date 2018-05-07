package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.CandidChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class CandidCommand extends BasicCommand
{
	@Override
	public int getScope()
	{
		return -5;
	}
	
	@Override
	public String getName()
	{
		return "Candidatures";
	}
	
	@Override
	public String getCommand()
	{
		return "c";
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <candidature>";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(event.getChannel().getType() != ChannelType.PRIVATE)
			Actions.deleteMessage(event.getMessage());
		TextChannel channel = new CandidChannelConfig().getTextChannel(event.getJDA());
		if(channel == null)
			return CommandResult.FAILED;
		else
		{
			Actions.sendMessage(channel, "@here Nouvelle candidature de %s: %s", event.getAuthor().getAsMention(), args.stream().collect(Collectors.joining(" ")));
			Actions.replyPrivate(event.getAuthor(), "Votre candidature a bien été transféré. Merci à vous.");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
