package fr.mrcraftcod.gunterdiscord.commands.musicparty;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.musicparty.MusicPartyListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
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
public class MusicPartySkipCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MusicPartySkipCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		MusicPartyListener.getParty(event.getGuild(), null, false).ifPresentOrElse(MusicPartyListener::skip, () -> Actions.reply(event, "Aucun évènement de ce type en cours"));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName()
	{
		return "Skip music";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("skip");
	}
	
	@Override
	public String getDescription()
	{
		return "Skip the current music in the party";
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
}
