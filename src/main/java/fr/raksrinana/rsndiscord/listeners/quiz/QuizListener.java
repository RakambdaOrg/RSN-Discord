package fr.raksrinana.rsndiscord.listeners.quiz;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuizListener extends ListenerAdapter implements Runnable{
	private static final ArrayList<QuizListener> quizzes = new ArrayList<>();
	private static final Pattern ANSWERS_DELIMITER = Pattern.compile("[,;]");
	@Getter
	private final Guild guild;
	private final Duration waitTime;
	private final LinkedList<Question> questions;
	private Message waitingMsg = null;
	private HashMap<User, Integer> answers;
	private boolean stopped;
	
	/**
	 * Constructor.
	 *
	 * @param guild  The guild.
	 * @param amount The amount of questions to add.
	 * @param delay  The delay before starting the quiz.
	 */
	private QuizListener(@NonNull final Guild guild, final int amount, final int delay){
		this.guild = guild;
		this.stopped = false;
		this.waitTime = Duration.ofSeconds(delay);
		this.questions = this.generateQuestions(amount);
		guild.getJDA().addEventListener(this);
		quizzes.add(this);
		final var executorService = Executors.newSingleThreadExecutor();
		executorService.submit(this);
		executorService.shutdown();
	}
	
	/**
	 * Pick some random questions from the CSV file.
	 *
	 * @param amount The maximum number of question.
	 *
	 * @return The questions.
	 */
	@NonNull
	private LinkedList<Question> generateQuestions(final int amount){
		final var lines = new LinkedList<String>();
		final var questionPath = Paths.get("./questions.csv").normalize().toAbsolutePath();
		try{
			lines.addAll(Files.readAllLines(questionPath));
		}
		catch(final Exception e){
			Log.getLogger(this.getGuild()).error("Error reading questions file {}", questionPath, e);
		}
		if(lines.isEmpty()){
			throw new IllegalStateException("No questions found");
		}
		Collections.shuffle(lines);
		final var list = new LinkedList<Question>();
		for(var i = 0; i < amount; i++){
			if(lines.isEmpty()){
				break;
			}
			final var line = ANSWERS_DELIMITER.split(lines.pop());
			final var correctAnswer = line[1];
			final var answersList = Arrays.stream(line, 2, line.length).filter(lineStr -> Objects.nonNull(lineStr) && !"".equalsIgnoreCase(lineStr.trim())).collect(Collectors.toList());
			Collections.shuffle(answersList);
			final var ID = ThreadLocalRandom.current().nextInt(0, answersList.size() + 1);
			final var answers = new HashMap<Integer, String>();
			for(var j = 0; j < answersList.size() + 1; j++){
				if(Objects.equals(j, ID)){
					answers.put(j, correctAnswer);
				}
				else{
					answers.put(j, answersList.get(j - (j > ID ? 1 : 0)));
				}
			}
			list.add(new Question(line[0], answers, ID));
		}
		return list;
	}
	
	/**
	 * Stop all quizzes.
	 */
	public static void stopAll(){
		quizzes.forEach(QuizListener::stop);
	}
	
	/**
	 * Stop the quiz.
	 */
	public void stop(){
		this.stopped = true;
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
	@NonNull
	public static Optional<QuizListener> getQuiz(@NonNull final Guild guild, final int amount, final int delay){
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
	@NonNull
	public static Optional<QuizListener> getQuiz(@NonNull final Guild guild, final int amount, final int delay, final boolean shouldCreate){
		return quizzes.stream().filter(q -> Objects.equals(q.getGuild(), guild)).findAny().or(() -> {
			try{
				if(shouldCreate){
					return Optional.of(new QuizListener(guild, amount, delay));
				}
			}
			catch(final Exception e){
				Log.getLogger(guild).error("Error create a new quiz game", e);
			}
			return Optional.empty();
		});
	}
	
	@Override
	public void run(){
		final var QUESTION_TIME = 20;
		try{
			final var quizChannelOptional = Settings.get(this.getGuild()).getQuizChannel().flatMap(ChannelConfiguration::getChannel);
			if(quizChannelOptional.isEmpty()){
				return;
			}
			final var quizChannel = quizChannelOptional.get();
			Actions.sendMessage(quizChannel, MessageFormat.format("Ok @here, I hope that you're hyped for a little quiz!\nIt's very simple: a question will appear with a set of possible answers. You can pick the answer you think correct by adding the corresponding reaction. You have {0}s to answer.\nEach correct answer will give you 1 point.\n\nWe're starting in {1}!", QUESTION_TIME, this.waitTime.toString().replace("PT", "")), null);
			try{
				Thread.sleep((this.waitTime.getSeconds() / 2) * 1000);
			}
			catch(final InterruptedException e){
				Log.getLogger(this.getGuild()).error("Error sleeping", e);
			}
			Actions.sendMessage(quizChannel, MessageFormat.format("{0} remaining!", this.waitTime.dividedBy(2).toString().replace("PT", "")), null);
			try{
				Thread.sleep((this.waitTime.getSeconds() / 2) * 1000);
			}
			catch(final InterruptedException e){
				Log.getLogger(this.guild).error("Error sleeping", e);
			}
			final var scores = new HashMap<User, Integer>();
			var i = 0;
			while(!this.stopped && !this.questions.isEmpty()){
				i++;
				try{
					final var question = this.questions.pop();
					final List<Character> emotes = new ArrayList<>();
					final var builder = new EmbedBuilder();
					builder.setAuthor(quizChannel.getJDA().getSelfUser().getName(), null, quizChannel.getJDA().getSelfUser().getAvatarUrl());
					builder.setColor(Color.YELLOW);
					builder.setTitle("Question " + i);
					builder.setDescription(question.getQuestion());
					question.getAnswers().keySet().stream().map(k -> {
						var emote = (char) ((int) 'a' + k);
						emotes.add(emote);
						return new MessageEmbed.Field(":regional_indicator_" + emote + ":", question.getAnswers().get(k), true);
					}).forEach(builder::addField);
					Actions.sendMessage(quizChannel, "", builder.build()).thenAccept(questionMessage -> {
						this.answers = new HashMap<>();
						this.waitingMsg = questionMessage;
						emotes.forEach(e -> Actions.addReaction(questionMessage, BasicEmotes.getEmote(String.valueOf(e)).getValue()));
					});
					try{
						Thread.sleep(QUESTION_TIME * 1000);
					}
					catch(final InterruptedException e){
						Log.getLogger(this.guild).error("Error sleeping", e);
					}
					Actions.sendMessage(quizChannel, "Stop!", null);
					Log.getLogger(this.getGuild()).info("Question over, answer was {}", question.getCorrectAnswer());
					this.waitingMsg = null;
					this.answers.forEach((user, v) -> {
						if(Objects.equals(v, question.getCorrectAnswerIndex())){
							final var newScore = scores.getOrDefault(user, 0) + 1;
							scores.put(user, newScore);
							Log.getLogger(this.getGuild()).info("{} +1 pt - now: {}", user, newScore);
						}
						else if(!scores.containsKey(user)){
							scores.put(user, 0);
						}
					});
					this.answers = null;
					try{
						Thread.sleep(5 * 1000);
					}
					catch(final InterruptedException e){
						Log.getLogger(this.getGuild()).error("Error sleeping", e);
					}
				}
				catch(final Exception e){
					Log.getLogger(this.getGuild()).error("Error quiz question", e);
				}
			}
			final HashMap<Integer, List<User>> allPositions;
			final var allScores = scores.values().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
			allPositions = allScores.stream().mapToInt(score -> score).boxed().collect(Collectors.toMap(score -> score, score -> new ArrayList<>(), (a, users) -> users, HashMap::new));
			scores.forEach((user, v) -> {
				if(allPositions.containsKey(v)){
					allPositions.get(v).add(user);
				}
			});
			final var builder = new EmbedBuilder();
			builder.setAuthor(quizChannel.getJDA().getSelfUser().getName(), null, quizChannel.getJDA().getSelfUser().getAvatarUrl());
			builder.setColor(Color.PINK);
			builder.setTitle("The quiz is now over!");
			builder.setDescription("Top scores:");
			allPositions.keySet().stream().sorted(Comparator.reverseOrder()).limit(3).map(v -> new MessageEmbed.Field("Position " + (1 + allScores.indexOf(v)) + " (" + v + " points)", allPositions.get(v).stream().map(User::getAsMention).collect(Collectors.joining(", ")), false)).forEach(builder::addField);
			Actions.sendMessage(quizChannel, "", builder.build());
			allPositions.forEach((score, users) -> {
				final var position = 1 + allScores.indexOf(score);
				final var format = "You finished the quiz #{0} with {1} points" + (users.size() > 1 ? ", you share this position with {2} other people" : "") + ".";
				final var message = MessageFormat.format(format, position, score, users.size() - 1);
				users.forEach(user -> Actions.replyPrivate(this.getGuild(), user, message, null));
			});
		}
		catch(final Exception e){
			Log.getLogger(this.getGuild()).error("Error quiz", e);
		}
		quizzes.remove(this);
		this.guild.getJDA().removeEventListener(this);
	}
	
	@Override
	public void onGuildMessageReactionAdd(@NonNull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			if(Objects.equals(event.getGuild(), this.getGuild()) && Objects.nonNull(this.waitingMsg) && Objects.equals(event.getMessageIdLong(), this.waitingMsg.getIdLong()) && !Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
				if(Objects.nonNull(this.answers)){
					final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(Objects.isNull(emote)){
						Actions.removeReaction(event.getReaction(), event.getUser());
						Actions.replyPrivate(event.getGuild(), event.getUser(), "Please use only letters.", null);
					}
					else{
						if(this.answers.containsKey(event.getUser())){
							Actions.removeReaction(event.getReaction(), event.getUser());
							Actions.replyPrivate(event.getGuild(), event.getUser(), "Please select only one reaction per question.", null);
						}
						else{
							this.answers.put(event.getUser(), mapEmote(emote));
						}
					}
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(this.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(@NonNull final GuildMessageReactionRemoveEvent event){
		super.onGuildMessageReactionRemove(event);
		try{
			if(Objects.equals(event.getGuild(), this.getGuild()) && Objects.nonNull(this.waitingMsg) && Objects.equals(event.getMessageIdLong(), this.waitingMsg.getIdLong())){
				if(Objects.nonNull(this.answers)){
					if(this.answers.containsKey(event.getUser())){
						final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
						if(Objects.equals(this.answers.get(event.getUser()), mapEmote(emote))){
							this.answers.remove(event.getUser());
							Log.getLogger(event.getGuild()).info("User {} removed answer", event.getUser());
						}
					}
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	/**
	 * Map an emote to a choice.
	 *
	 * @param name The emote.
	 *
	 * @return The choice selected.
	 */
	private static int mapEmote(final BasicEmotes name){
		return Objects.isNull(name) ? -1 : name.name().toLowerCase().charAt(0) - 'a';
	}
}
