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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class CandidatureCommand extends BasicCommand
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
	public List<String> getCommand()
	{
		return List.of("candidature", "c");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <candidature>";
	}
	
	@Override
	public String getDescription()
	{
		return "Propose uen candidature pour des élections";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(event.getChannel().getType() != ChannelType.PRIVATE)
			Actions.deleteMessage(event.getMessage());
		if(args.size() < 1)
		{
			Actions.replyPrivate(event.getAuthor(), "Veuillez entrer un texte de candidature: " + getCommandUsage());
		}
		else
		{
			TextChannel channel = new CandidChannelConfig().getTextChannel(event.getJDA());
			if(channel == null)
				return CommandResult.FAILED;
			else
			{
				Actions.sendMessage(channel, "@here Nouvelle candidature de %s: %s", event.getAuthor().getAsMention(), args.stream().collect(Collectors.joining(" ")));
				Actions.replyPrivate(event.getAuthor(), "Votre candidature a bien été transférée. Merci à vous.");
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
