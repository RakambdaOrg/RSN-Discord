package fr.mrcraftcod.gunterdiscord.commands.generic;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public abstract class BasicCommand implements Command
{
	private final Command parent;
	
	/**
	 * Constructor.
	 */
	public BasicCommand()
	{
		this(null);
	}
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public BasicCommand(Command parent)
	{
		this.parent = parent;
	}
	
	@Override
	public String getCommandUsage()
	{
		return getParent() == null || getParent() instanceof CompositeCommand ? "" : getParent().getCommandUsage();
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(!isAllowed(event.getMember()))
			throw new NotAllowedException();
		return CommandResult.SUCCESS;
	}
	
	@Override
	public Command getParent()
	{
		return parent;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
	}
}
