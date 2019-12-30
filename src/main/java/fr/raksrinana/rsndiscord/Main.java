package fr.raksrinana.rsndiscord;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import fr.raksrinana.rsndiscord.listeners.*;
import fr.raksrinana.rsndiscord.listeners.quiz.QuizListener;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.runners.*;
import fr.raksrinana.rsndiscord.runners.anilist.AniListMediaListScheduledRunner;
import fr.raksrinana.rsndiscord.runners.anilist.AniListNotificationScheduledRunner;
import fr.raksrinana.rsndiscord.runners.trakt.TraktUserHistoryScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.reaction.*;
import fr.raksrinana.rsndiscord.utils.reminder.AnilistReleaseReminderHandler;
import fr.raksrinana.rsndiscord.utils.reminder.DefaultReminderHandler;
import fr.raksrinana.rsndiscord.utils.reminder.ReminderUtils;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	public static boolean DEVELOPMENT = Boolean.parseBoolean(System.getProperty("rsndev", "false"));
	@Getter
	private static CLIParameters parameters;
	@Getter
	private static JDA jda;
	private static ConsoleHandler consoleHandler;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(@NonNull final String[] args){
		parameters = Optional.ofNullable(loadEnv(args)).orElseThrow(() -> new IllegalStateException("Failed to load environment"));
		try{
			Log.getLogger(null).info("Building JDA");
			final var jdaBuilder = new JDABuilder(AccountType.BOT).setToken(System.getProperty("RSN_TOKEN"));
			jdaBuilder.addEventListeners(new CommandsMessageListener());
			jdaBuilder.addEventListeners(new ShutdownListener());
			jdaBuilder.addEventListeners(new LogListener());
			jdaBuilder.addEventListeners(new AutoRolesListener());
			jdaBuilder.addEventListeners(new IdeaChannelMessageListener());
			jdaBuilder.addEventListeners(new ReactionListener());
			jdaBuilder.addEventListeners(new ReplyMessageListener());
			jdaBuilder.setAutoReconnect(true);
			jda = jdaBuilder.build();
			jda.awaitReady();
			jda.getPresence().setActivity(Activity.playing("g?help for the help"));
			Log.getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(Settings::get).count());
			Log.getLogger(null).info("Adding handlers");
			ReminderUtils.addHandler(new DefaultReminderHandler());
			ReminderUtils.addHandler(new AnilistReleaseReminderHandler());
			ReactionUtils.addHandler(new DefaultReactionHandler());
			ReactionUtils.addHandler(new AcceptedQuestionReactionHandler());
			ReactionUtils.addHandler(new AnilistTodosReactionHandler());
			ReactionUtils.addHandler(new TodosReactionHandler());
			Log.getLogger(null).info("Creating runners");
			final var scheduledRunners = List.of(new RemoveRolesScheduledRunner(jda), new AniListNotificationScheduledRunner(jda), new AniListMediaListScheduledRunner(jda), new SaveConfigScheduledRunner(), new DisplayDailyStatsScheduledRunner(jda), new OverwatchLeagueScheduledRunner(jda), new RemindersScheduledRunner(jda), new TraktUserHistoryScheduledRunner(jda));
			for(final var scheduledRunner : scheduledRunners){
				executorService.scheduleAtFixedRate(scheduledRunner, scheduledRunner.getDelay(), scheduledRunner.getPeriod(), scheduledRunner.getPeriodUnit());
			}
			Log.getLogger(null).info("Started");
			announceStart();
			restartTwitchIRCConnections();
		}
		catch(final LoginException | InterruptedException e){
			Log.getLogger(null).error("Couldn't start bot", e);
			System.exit(1);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Log.getLogger(null).info("Shutdown hook triggered");
			Settings.close();
		}));
		Log.getLogger(null).info("Shutdown hook registered");
		consoleHandler = new ConsoleHandler();
		consoleHandler.start();
		jda.getGuilds().forEach(guild -> {
			final var settings = Settings.get(guild);
			for(final var todo : settings.getTodos()){
				settings.getMessagesAwaitingReaction().add(new WaitingReactionMessageConfiguration(todo.getMessage(), ReactionTag.TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(todo.isDeleteOnDone()))));
			}
			settings.getTodos().clear();
		});
	}
	
	static CLIParameters loadEnv(@NonNull String[] args){
		Log.getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			Log.getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		final var parameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(parameters).build().parse(args);
		}
		catch(final ParameterException e){
			Log.getLogger(null).error("Failed to parse arguments", e);
			e.usage();
			return null;
		}
		final var prop = new Properties();
		try(final var is = Files.newInputStream(parameters.getConfigurationFile())){
			prop.load(is);
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
		}
		prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
		Log.getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		Unirest.config().setObjectMapper(new ObjectMapper(){
			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			
			@Override
			public <T> T readValue(String value, Class<T> valueType){
				try{
					return this.jacksonObjectMapper.readValue(value, valueType);
				}
				catch(IOException var4){
					throw new RuntimeException(var4);
				}
			}
			
			@Override
			public <T> T readValue(String value, GenericType<T> genericType){
				try{
					return this.jacksonObjectMapper.readValue(value, new TypeReference<>(){});
				}
				catch(IOException var4){
					throw new RuntimeException(var4);
				}
			}
			
			@Override
			public String writeValue(Object value){
				try{
					return this.jacksonObjectMapper.writeValueAsString(value);
				}
				catch(JsonProcessingException var3){
					throw new RuntimeException(var3);
				}
			}
		});
		return parameters;
	}
	
	/**
	 * Get the version of the bot.
	 *
	 * @return The version, or {@code "Unknown"} if unknown.
	 */
	@NonNull
	public static String getRSNBotVersion(){
		final var properties = new Properties();
		try{
			properties.load(Main.class.getResource("/version.properties").openStream());
		}
		catch(final Exception e){
			Log.getLogger(null).warn("Error reading version", e);
		}
		return properties.getProperty("bot.version", "Unknown");
	}
	
	/**
	 * Announce the bot started in channels defined in the configuration.
	 *
	 * @see GuildConfiguration#getAnnounceStartChannel()
	 */
	private static void announceStart(){
		Main.jda.getGuilds().stream().map(Settings::get).map(GuildConfiguration::getAnnounceStartChannel).flatMap(Optional::stream).map(ChannelConfiguration::getChannel).flatMap(Optional::stream).forEach(channel -> Actions.sendMessage(channel, "Bot started :)", null));
	}
	
	/**
	 * Connects to IRC channels defined in the configuration.
	 *
	 * @see GuildConfiguration#getTwitchAutoConnectUsers()
	 */
	private static void restartTwitchIRCConnections(){
		Main.getJda().getGuilds().forEach(guild -> Settings.get(guild).getTwitchAutoConnectUsers().forEach(user -> {
			try{
				TwitchIRC.connect(guild, user);
			}
			catch(IOException e){
				Log.getLogger(guild).error("Failed to automatically connect to twitch user {}", user, e);
			}
		}));
	}
	
	/**
	 * Close the bot.
	 */
	public static void close(){
		QuizListener.stopAll();
		TraktUtils.stopAll();
		ReplyMessageListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchIRC.close();
		executorService.shutdownNow();
		consoleHandler.close();
		Settings.close();
		Main.getJda().shutdown();
		final var client = Main.getJda().getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
}
