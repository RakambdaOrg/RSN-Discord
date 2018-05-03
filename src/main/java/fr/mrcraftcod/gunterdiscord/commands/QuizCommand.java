package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.Question;
import fr.mrcraftcod.gunterdiscord.listeners.QuizMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class QuizCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(args.size() > 0)
		{
			switch(args.pop())
			{
				case "start":
					QuizMessageListener quiz = QuizMessageListener.getInstance(generateQuestions(args.size() > 0 ? Integer.parseInt(args.pop()) : Integer.MAX_VALUE));
					if(quiz != null)
						new Thread(quiz).start();
					break;
				case "stop":
					QuizMessageListener.setBack();
					break;
			}
		}
		return CommandResult.SUCCESS;
	}
	
	/**
	 * Pick some random questions from the CSV file.
	 *
	 * @param amount The maximum number of question.
	 *
	 * @return The questions.
	 */
	private LinkedList<Question> generateQuestions(int amount)
	{
		LinkedList<String> lines = new LinkedList<>();
		try
		{
			lines.addAll(Files.readAllLines(Paths.get(Main.class.getResource("/quiz/questions.csv").toURI())));
		}
		catch(IOException | URISyntaxException e)
		{
			Log.error("Error reading questions file", e);
		}
		
		Collections.shuffle(lines);
		LinkedList<Question> list = new LinkedList<>();
		for(int i = 0; i < amount; i++)
		{
			if(lines.size() < 1)
				break;
			String[] line = lines.pop().split(",");
			String correctAnswer = line[1];
			
			List<String> answersList = Arrays.stream(line, 2, line.length).filter(l -> l != null && !l.trim().equalsIgnoreCase("")).collect(Collectors.toList());
			Collections.shuffle(answersList);
			int ID = ThreadLocalRandom.current().nextInt(0, answersList.size() + 1);
			HashMap<Integer, String> answers = new HashMap<>();
			for(int j = 0; j < answersList.size() + 1; j++)
				if(j == ID)
					answers.put(j, correctAnswer);
				else
					answers.put(j, answersList.get(j - (j > ID ? 1 : 0)));
			list.add(new Question(line[0], answers, ID));
		}
		return list;
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <action> [params]";
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
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
	public String getCommand()
	{
		return "quiz";
	}
}
