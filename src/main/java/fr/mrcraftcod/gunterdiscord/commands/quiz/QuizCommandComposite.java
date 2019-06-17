package fr.mrcraftcod.gunterdiscord.commands.quiz;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class QuizCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public QuizCommandComposite(){
		super();
		addSubCommand(new QuizStartCommand(this));
		addSubCommand(new QuizStopCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Override
	public String getName(){
		return "Quiz";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("quiz");
	}
	
	@Override
	public String getDescription(){
		return "Management of the quiz";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
