package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuizChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-19
 */
public class QuizMessageListener extends ListenerAdapter implements Runnable
{
	private static QuizMessageListener INSTANCE = null;
	private static Message waitingMsg = null;
	private static HashMap<Long, Integer> answers;
	private boolean done = false;
	private LinkedList<Question> questions;
	
	public QuizMessageListener()
	{
	
	}
	
	public QuizMessageListener(LinkedList<Question> questions)
	{
		this.questions = questions;
	}
	
	public static QuizMessageListener getInstance(LinkedList<Question> questions)
	{
		if(INSTANCE == null || INSTANCE.isDone())
			return (INSTANCE = new QuizMessageListener(questions));
		return null;
	}
	
	private boolean isDone()
	{
		return done;
	}
	
	@Override
	public void run()
	{
		int HALF_WAIT_TIME = 30;
		int QUESTION_TIME = 20;
		try
		{
			JDA jda = Main.getJDA();
			jda.getPresence().setGame(Game.playing("The Kwizzz"));
			TextChannel quizChannel = null;
			
			try
			{
				quizChannel = jda.getTextChannelById(new QuizChannelConfig().getLong());
			}
			catch(InvalidClassException | NoValueDefinedException e)
			{
				e.printStackTrace();
			}
			
			if(quizChannel == null)
			{
				setBack();
				return;
			}
			
			Actions.sendMessage(quizChannel, "Ok @here, j'espère que vous êtes aussi chaud que mon chalumeau pour un petit kwizzz!\n" + "Le principe est simple: une question va apparaitre avec un set de réponses possibles. Vous pouvez répondre à la question en ajoutant une réaction avec la lettre corespondante. Le temps limite pour répondre est de " + QUESTION_TIME + "s.\n" + "Chaque bonne réponse vous donnera 1 point.\n\nOn commence dans " + (HALF_WAIT_TIME * 2) + " secondes!");
			try
			{
				Thread.sleep(HALF_WAIT_TIME * 1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			Actions.sendMessage(quizChannel, "Encore " + HALF_WAIT_TIME + " secondes! " + Utilities.getEmoteMention("cookie"));
			
			try
			{
				Thread.sleep(HALF_WAIT_TIME * 1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
			HashMap<Long, Integer> scores = new HashMap<>();
			int i = 0;
			while(!done && questions.size() > 0)
			{
				i++;
				try
				{
					Question question = questions.pop();
					List<Character> emotes = new ArrayList<>();
					Message questionMessage = quizChannel.sendMessage("Question " + i + ": " + question.getQuestion() + "\n" + question.getAnswers().keySet().stream().map(k -> {
						char emote = (char) ((int) 'a' + k);
						emotes.add(emote);
						return ":regional_indicator_" + emote + ":: " + question.getAnswers().get(k);
					}).collect(Collectors.joining("\n"))).complete();
					answers = new HashMap<>();
					waitingMsg = questionMessage;
					emotes.forEach(e -> questionMessage.addReaction(BasicEmotes.getEmote("" + e).getValue()).queue());
					
					try
					{
						Thread.sleep(QUESTION_TIME * 1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					quizChannel.sendMessage("Stoooooooooooooopu!").complete();
					System.out.println("Question over, answer was " + question.getCorrectAnswerIndex());
					waitingMsg = null;
					answers.forEach((k, v) -> {
						if(v == question.getCorrectAnswerIndex())
						{
							int newScore = scores.getOrDefault(k, 0) + 1;
							scores.put(k, newScore);
							System.out.println(k + " +1 pt - now: " + newScore);
						}
						else if(!scores.containsKey(k))
							scores.put(k, 0);
					});
					try(PrintWriter fos = new PrintWriter(System.currentTimeMillis() + ".txt"))
					{
						fos.println("Question: " + question.getQuestion());
						fos.println("Reponses: ");
						for(int j = 0; j < question.getAnswers().size(); j++)
							fos.println("\t" + (j == question.getCorrectAnswerIndex() ? "-> " : "") + "\t" + j + " " + question.getAnswers().get(j));
						fos.println();
						answers.forEach((k, v) -> {
							User user = jda.getUserById(k);
							fos.println(user.getName() + "#" + user.getDiscriminator() + " -> " + v + " : " + question.getAnswers().get(v));
						});
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					answers = null;
					
					try
					{
						Thread.sleep(5 * 1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			HashMap<Integer, List<String>> bests = new HashMap<>();
			List<Integer> bestsScores = scores.values().stream().sorted(Comparator.reverseOrder()).limit(5).collect(Collectors.toList());
			for(int score : bestsScores)
				bests.put(score, new ArrayList<>());
			scores.forEach((k, v) -> {
				if(bests.containsKey(v))
					bests.get(v).add(jda.getUserById(k).getAsMention());
			});
			quizChannel.sendMessage("Le jeu est terminé! Voici le top des scores:\n" + bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> "" + (1 + bestsScores.indexOf(v)) + " (" + v + " points): " + bests.get(v).stream().collect(Collectors.joining(", "))).collect(Collectors.joining("\n"))).queue();
			setBack();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void setBack()
	{
		JDA jda = Main.getJDA();
		jda.getPresence().setGame(Game.playing("Le chalumeau"));
		if(INSTANCE != null)
			INSTANCE.done = true;
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		super.onMessageReactionAdd(event);
		try
		{
			System.out.println(event.getUser().getName() + " --- " + event.getMessageIdLong() + " - " + (waitingMsg == null ? "NULL" : waitingMsg.getIdLong()) + " - " + event.getUser().getIdLong() + " - " + event.getJDA().getSelfUser().getIdLong());
			
			if(waitingMsg != null && event.getMessageIdLong() == waitingMsg.getIdLong() && event.getUser().getIdLong() != event.getJDA().getSelfUser().getIdLong())
			{
				if(answers != null)
				{
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == null)
					{
						event.getReaction().removeReaction(event.getUser()).queue();
						event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Merci de n'utilser que les lettres.").queue());
					}
					else
					{
						if(answers.containsKey(event.getUser().getIdLong()))
						{
							event.getReaction().removeReaction(event.getUser()).queue();
							event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Merci de ne mettre qu'une seule réaction par question.").queue());
							System.out.println("User " + event.getUser().getAsMention() + " added answer AGAIN " + emote.name() + " - not counted");
						}
						else
						{
							answers.put(event.getUser().getIdLong(), mapEmote(emote));
							System.out.println("User " + event.getUser().getAsMention() + " added answer for " + emote.name());
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event)
	{
		super.onMessageReactionRemove(event);
		try
		{
			if(waitingMsg != null && event.getMessageIdLong() == waitingMsg.getIdLong())
			{
				if(answers != null)
				{
					if(answers.containsKey(event.getUser().getIdLong()))
					{
						BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
						if(answers.get(event.getUser().getIdLong()) == mapEmote(emote))
						{
							answers.remove(event.getUser().getIdLong());
							System.out.println("User " + event.getUser().getAsMention() + " removed answer");
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private int mapEmote(BasicEmotes name)
	{
		return name == null ? -1 : name.name().toLowerCase().charAt(0) - 'a';
	}
}
