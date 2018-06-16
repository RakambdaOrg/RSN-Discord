package fr.mrcraftcod.gunterdiscord.listeners.quiz;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuizChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-19
 */
public class QuizListener extends ListenerAdapter implements Runnable
{
	private static final ArrayList<QuizListener> quizzes = new ArrayList<>();
	private Duration waitTime;
	private final Guild guild;
	private Message waitingMsg = null;
	private HashMap<Long, Integer> answers;
	private LinkedList<Question> questions;
	private boolean stopped;
	
	/**
	 * Constructor.
	 *
	 * @param guild  The guild.
	 * @param amount The amount of questions to add.
	 * @param delay  The delay before starting the quiz.
	 */
	private QuizListener(Guild guild, int amount, int delay)
	{
		this.guild = guild;
		this.stopped = false;
		waitTime = Duration.ofSeconds(delay);
		
		questions = generateQuestions(amount);
		
		guild.getJDA().addEventListener(this);
		quizzes.add(this);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(this);
		executorService.shutdown();
	}
	
	/**
	 * Stop all quizzes.
	 */
	public static void stopAll()
	{
		quizzes.forEach(QuizListener::stop);
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
			lines.addAll(Files.readAllLines(Paths.get("./questions.csv")));
		}
		catch(Exception e)
		{
			Log.error("Error reading questions file", e2);
		}
		
		if(lines.isEmpty())
			throw new IllegalStateException("No questions found");
		
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
	
	/**
	 * Get the current instance of the game.
	 *
	 * @param guild  The guild.
	 * @param amount The amount of questions.
	 * @param delay  The delay before starting the quiz.
	 *
	 * @return The game of the guild.
	 */
	public static Optional<QuizListener> getQuiz(Guild guild, int amount, int delay)
	{
		return getQuiz(guild, amount, delay, true);
	}
	
	/**
	 * Get the current instance of the game.
	 *
	 * @param guild        The guild.
	 * @param amount       The amount of questions.
	 * @param shouldCreate If a new game should be created if not found.
	 * @param delay        The delay before starting the quiz.
	 *
	 * @return The game of the guild.
	 */
	public static Optional<QuizListener> getQuiz(Guild guild, int amount, int delay, boolean shouldCreate)
	{
		return quizzes.stream().filter(q -> q.getGuild().getIdLong() == guild.getIdLong()).findAny().or(() -> {
			try
			{
				if(shouldCreate)
					return Optional.of(new QuizListener(guild, amount, delay));
			}
			catch(Exception e)
			{
				Log.error("Error create a new quiz game", e);
			}
			return Optional.empty();
		});
	}
	
	/**
	 * Get the guild.
	 *
	 * @return The guild.
	 */
	private Guild getGuild()
	{
		return guild;
	}
	
	/**
	 * Stop the quiz.
	 */
	public void stop()
	{
		stopped = true;
	}
	
	@Override
	public void run()
	{
		int QUESTION_TIME = 20;
		try
		{
			JDA jda = Main.getJDA();
			jda.getPresence().setGame(Game.playing("The Kwizzz"));
			TextChannel quizChannel = new QuizChannelConfig().getTextChannel(guild);
			
			if(quizChannel == null)
				return;
			
			Actions.sendMessage(quizChannel, "Ok @here, j'espère que vous êtes aussi chaud que mon chalumeau pour un petit kwizzz!\nLe principe est simple: une question va apparaitre avec un set de réponses possibles. Vous pouvez répondre à la question en ajoutant une réaction avec la lettre corespondante. Le temps limite pour répondre est de %ds.\n" + "Chaque bonne réponse vous donnera 1 point.\n\nOn commence dans %s!", QUESTION_TIME, waitTime.toString().replace("PT", ""));
			try
			{
				Thread.sleep((waitTime.getSeconds() / 2) * 1000);
			}
			catch(InterruptedException e)
			{
				Log.error("Error sleeping", e);
			}
			
			Actions.sendMessage(quizChannel, "Encore %s!", waitTime.dividedBy(2).toString().replace("PT", ""));
			
			try
			{
				Thread.sleep((waitTime.getSeconds() / 2) * 1000);
			}
			catch(InterruptedException e)
			{
				Log.error("Error sleeping", e);
			}
			
			HashMap<Long, Integer> scores = new HashMap<>();
			int i = 0;
			while(!stopped && questions.size() > 0)
			{
				i++;
				try
				{
					Question question = questions.pop();
					List<Character> emotes = new ArrayList<>();
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(quizChannel.getJDA().getSelfUser().getName(), null, quizChannel.getJDA().getSelfUser().getAvatarUrl());
					builder.setColor(Color.YELLOW);
					builder.setTitle("Question " + i);
					builder.setDescription(question.getQuestion());
					question.getAnswers().keySet().stream().map(k -> {
						char emote = (char) ((int) 'a' + k);
						emotes.add(emote);
						return new MessageEmbed.Field(":regional_indicator_" + emote + ":", question.getAnswers().get(k), true);
					}).forEach(builder::addField);
					
					Message questionMessage = Actions.getMessage(quizChannel, builder.build());
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
			for(int score: bestsScores)
				bests.put(score, new ArrayList<>());
			scores.forEach((k, v) -> {
				if(bests.containsKey(v))
					bests.get(v).add(jda.getUserById(k).getAsMention());
			});
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(quizChannel.getJDA().getSelfUser().getName(), null, quizChannel.getJDA().getSelfUser().getAvatarUrl());
			builder.setColor(Color.PINK);
			builder.setTitle("Le jeu est terminé!");
			builder.setDescription("Voici le top des scores:");
			bests.keySet().stream().sorted(Comparator.reverseOrder()).map(v -> new MessageEmbed.Field("Position " + (1 + bestsScores.indexOf(v)) + " (" + v + " points)", String.join(", ", bests.get(v)), false)).forEach(builder::addField);
			Actions.sendMessage(quizChannel, builder.build());
		}
		catch(Exception e)
		{
			Log.error("Error quiz", e);
		}
		quizzes.remove(this);
		guild.getJDA().removeEventListener(this);
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		super.onMessageReactionAdd(event);
		try
		{
			if(event.getGuild().getIdLong() == getGuild().getIdLong() && waitingMsg != null && event.getMessageIdLong() == waitingMsg.getIdLong() && event.getUser().getIdLong() != event.getJDA().getSelfUser().getIdLong())
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
			if(event.getGuild().getIdLong() == getGuild().getIdLong() && waitingMsg != null && event.getMessageIdLong() == waitingMsg.getIdLong())
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
