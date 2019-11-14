package fr.raksrinana.rsndiscord.commands.quiz;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

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
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Quiz";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("quiz");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Management of the quiz";
	}
}
