package fr.mrcraftcod.gunterdiscord.commands.hangman;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class HangmanLetterCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	HangmanLetterCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <lettre>";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		HangmanListener.getGame(event.getGuild(), false).ifPresent(h -> h.guess(event.getMember(), args.isEmpty() ? '#' : args.poll().charAt(0)));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Lettre", "La lettre que vous proposez", true);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Lettre pendu";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("l", "lettre");
	}
	
	@Override
	public String getDescription()
	{
		return "Propose une lettre au pendu";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
