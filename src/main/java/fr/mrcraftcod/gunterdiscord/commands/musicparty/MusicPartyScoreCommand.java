package fr.mrcraftcod.gunterdiscord.commands.musicparty;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.MusicPartyListener;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyScoreCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MusicPartyScoreCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		MusicPartyListener.getParty(event.getGuild(), null, false).ifPresent(MusicPartyListener::printScores);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Scores music party";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("scores");
	}
	
	@Override
	public String getDescription()
	{
		return "Print the scores of the event";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
}
