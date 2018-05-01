package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.configs.ReportChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
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
	public String getCommand()
	{
		return "report";
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <raison>";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(event.getChannel().getType() != ChannelType.PRIVATE)
		{
			event.getMessage().delete().complete();
			return CommandResult.SUCCESS;
		}
		long reportChannel = new ReportChannelConfig().getLong();
		if(reportChannel < 0)
		{
			Actions.sendMessage(event.getPrivateChannel(), "Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.");
		}
		else
		{
			Actions.sendMessage(event.getPrivateChannel(), "Votre message a bien été transféré. Merci à vous.");
			event.getJDA().getTextChannelById(reportChannel).sendMessageFormat("%s Nouveau report de %s#%s: %s", Utilities.getRole(event.getJDA(), "Kaporal (modo)").getAsMention(), event.getAuthor().getAsMention(), event.getAuthor().getDiscriminator(), args.stream().collect(Collectors.joining(" "))).complete();
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
