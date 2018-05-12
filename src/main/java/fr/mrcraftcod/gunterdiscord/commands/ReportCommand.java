package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.ReportChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class ReportCommand extends BasicCommand
{
	@Override
	public int getScope()
	{
		return -5;
	}
	
	@Override
	public String getName()
	{
		return "Report";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("report", "r");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <raison>";
	}
	
	@Override
	public String getDescription()
	{
		return "Envoi un message aux modérateurs";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		TextChannel channel = new ReportChannelConfig().getTextChannel(event.getJDA());
		if(channel == null)
			Actions.replyPrivate(event.getAuthor(), "Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.");
		else
		{
			Actions.sendMessage(channel, "@here Nouveau report de %s#%s: %s", event.getAuthor().getAsMention(), event.getAuthor().getDiscriminator(), args.stream().collect(Collectors.joining(" ")));
			Actions.replyPrivate(event.getAuthor(), "Votre message a bien été transféré. Merci à vous.");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
