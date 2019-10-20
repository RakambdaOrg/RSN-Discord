package fr.raksrinana.rsndiscord.commands.quiz;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@BotCommand
public class QuizCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public QuizCommandComposite(){
		super();
		this.addSubCommand(new QuizStartCommand(this));
		this.addSubCommand(new QuizStopCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Quiz";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("quiz");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Management of the quiz";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
