package fr.mrcraftcod.gunterdiscord.commands.quiz;

import fr.mrcraftcod.gunterdiscord.commands.generic.CompositeCommand;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class QuizCompositeCommand extends CompositeCommand
{
	/**
	 * Constructor.
	 */
	public QuizCompositeCommand()
	{
		super();
		addSubCommand(new QuizStartCommand(this));
		addSubCommand(new QuizStopCommand(this));
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Quiz";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("quiz");
	}
	
	@Override
	public String getDescription()
	{
		return "Management of the quiz";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
}
