package fr.mrcraftcod.gunterdiscord.commands.quiz;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
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
public class QuizStartCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QuizStartCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [quantité]";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		int amount = Integer.MAX_VALUE;
		if(!args.isEmpty())
			try
			{
				amount = Integer.parseInt(args.poll());
			}
			catch(Exception ignored)
			{
			}
		QuizListener.getQuiz(event.getGuild(), amount);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Quantité", "Le nombre de questions (par défaut le maximum disponible)", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Start quiz";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("start");
	}
	
	@Override
	public String getDescription()
	{
		return "Start a new quiz";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
}
