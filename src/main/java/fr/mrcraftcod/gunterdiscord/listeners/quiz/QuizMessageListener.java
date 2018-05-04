package fr.mrcraftcod.gunterdiscord.listeners.quiz;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuizChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
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
	
	/**
	 * Constructor.
	 */
	public QuizMessageListener()
	{
	}
	
	/**
	 * Constructor of the game.
	 *
	 * @param questions The questions to set.
	 */
	private QuizMessageListener(LinkedList<Question> questions)
	{
		this.questions = questions;
	}
	
	/**
	 * Get the current instance of teh game.
	 *
	 * @param questions The questions to set if a new game is started.
	 *
	 * @return A new game or null is one is already running.
	 */
	public static QuizMessageListener getInstance(LinkedList<Question> questions)
	{
		if(INSTANCE == null || INSTANCE.isDone())
			return (INSTANCE = new QuizMessageListener(questions));
		return null;
	}
	
	/**
	 * Tell is this game is over.
	 *
	 * @return True if over, false otherwise.
	 */
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
			TextChannel quizChannel = new QuizChannelConfig().getTextChannel(jda);
			
			if(quizChannel == null)
			{
				setBack();
				return;
			}
			
			Actions.sendMessage(quizChannel, "Ok @here, j'espère que vous êtes aussi chaud que mon chalumeau pour un petit kwizzz!\n" + "Le principe est simple: une question va apparaitre avec un set de réponses possibles. Vous pouvez répondre à la question en ajoutant une réaction avec la lettre corespondante. Le temps limite pour répondre est de %ds.\n" + "Chaque bonne réponse vous donnera 1 point.\n\nOn commence dans %d secondes!", QUESTION_TIME, HALF_WAIT_TIME * 2);
			try
			{
				Thread.sleep(HALF_WAIT_TIME * 1000);
			}
			catch(InterruptedException e)
			{
				Log.error("Error sleeping", e);
			}
			
			Actions.sendMessage(quizChannel, "Encore %d secondes! %d", HALF_WAIT_TIME, Utilities.getEmoteMention("cookie"));
			
			try
			{
				Thread.sleep(HALF_WAIT_TIME * 1000);
			}
			catch(InterruptedException e)
			{
				Log.error("Error sleeping", e);
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
					Message questionMessage = Actions.getMessage(quizChannel, "Question %d: %s\n%s", i, question.getQuestion(), question.getAnswers().keySet().stream().map(k -> {
						char emote = (char) ((int) 'a' + k);
						emotes.add(emote);
						return ":regional_indicator_" + emote + ":: " + question.getAnswers().get(k);
					}).collect(Collectors.joining("\n")));
					answers = new HashMap<>();
					waitingMsg = questionMessage;
					emotes.forEach(e -> questionMessage.addReaction(BasicEmotes.getEmote("" + e).getValue()).queue());
					
					try
					{
						Thread.sleep(QUESTION_TIME * 1000);
					}
					catch(InterruptedException e)
					{
						Log.error("Error sleeping", e);
					}
					Actions.sendMessage(quizChannel, "Stoooooooooooooopu!");
					Log.info("Question over, answer was " + question.getCorrectAnswerIndex());
					waitingMsg = null;
					answers.forEach((k, v) -> {
						if(v == question.getCorrectAnswerIndex())
						{
							int newScore = scores.getOrDefault(k, 0) + 1;
							scores.put(k, newScore);
							Log.info(k + " +1 pt - now: " + newScore);
						}
						else if(!scores.containsKey(k))
							scores.put(k, 0);
					});
					try(PrintWriter fos = new PrintWriter(System.currentTimeMillis() + ".txt"))
					{
						fos.println("Question: " + question.getQuestion());
						fos.println("Réponses: ");
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
						Log.error("Error writing quiz question file", e);
					}
					answers = null;
					
					try
					{
						Thread.sleep(5 * 1000);
					}
					catch(InterruptedException e)
					{
						Log.error("Error sleeping", e);
					}
				}
				catch(Exception e)
				{
					Log.error("Error quiz question", e);
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
			Actions.sendMessage(quizChannel, "Le jeu est terminé! Voici le top des scores:\n%s", bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> "" + (1 + bestsScores.indexOf(v)) + " (" + v + " points): " + bests.get(v).stream().collect(Collectors.joining(", "))).collect(Collectors.joining("\n")));
			setBack();
		}
		catch(Exception e)
		{
			Log.error("Error quiz", e);
		}
	}
	
	/**
	 * Resets the game.
	 */
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
			Log.info("New reaction " + event.getReaction().getReactionEmote().getName() + " from `" + Actions.getUserToLog(event.getUser()) + "` in " + event.getReaction().getTextChannel().getName() + " on `" + event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).complete().getContentRaw());
		}
		catch(NullPointerException ignored)
		{
		}
		
		try
		{
			if(waitingMsg != null && event.getMessageIdLong() == waitingMsg.getIdLong() && event.getUser().getIdLong() != event.getJDA().getSelfUser().getIdLong())
			{
				if(answers != null)
				{
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == null)
					{
						event.getReaction().removeReaction(event.getUser()).queue();
						Actions.replyPrivate(event.getUser(), "Merci de n'utilser que les lettres.");
					}
					else
					{
						if(answers.containsKey(event.getUser().getIdLong()))
						{
							event.getReaction().removeReaction(event.getUser()).queue();
							Actions.replyPrivate(event.getUser(), "Merci de ne mettre qu'une seule réaction par question.");
						}
						else
							answers.put(event.getUser().getIdLong(), mapEmote(emote));
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.error("", e);
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
							Log.info("User " + event.getUser().getAsMention() + " removed answer");
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
	
	/**
	 * Map an emote to a choice.
	 *
	 * @param name The emote.
	 *
	 * @return The choice selected.
	 */
	private int mapEmote(BasicEmotes name)
	{
		return name == null ? -1 : name.name().toLowerCase().charAt(0) - 'a';
	}
}
