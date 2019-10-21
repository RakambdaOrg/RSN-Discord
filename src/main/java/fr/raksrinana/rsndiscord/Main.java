package fr.raksrinana.rsndiscord;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.raksrinana.rsndiscord.listeners.*;
import fr.raksrinana.rsndiscord.listeners.quiz.QuizListener;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.runners.DisplayDailyStatsScheduledRunner;
import fr.raksrinana.rsndiscord.runners.OverwatchLeagueScheduledRunner;
import fr.raksrinana.rsndiscord.runners.RemoveRolesScheduledRunner;
import fr.raksrinana.rsndiscord.runners.SaveConfigScheduledRunner;
import fr.raksrinana.rsndiscord.runners.anilist.AniListMediaUserListScheduledRunner;
import fr.raksrinana.rsndiscord.runners.anilist.AniListNotificationScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.irc.TwitchIRC;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Main{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	public static boolean DEVELOPMENT = Boolean.parseBoolean(System.getProperty("rsndev", "false"));
	private static JDA jda;
	private static ConsoleHandler consoleHandler;
	public static CLIParameters parameters;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(@Nonnull final String[] args){
		Log.getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			Log.getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		parameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(parameters).build().parse(args);
		}
		catch(final ParameterException e){
			Log.getLogger(null).error("Failed to parse arguments", e);
			e.usage();
			return;
		}
		if(Objects.nonNull(parameters.getConfigurationFile())){
			final var prop = new Properties();
			try(final var is = new FileInputStream(parameters.getConfigurationFile())){
				prop.load(is);
			}
			catch(final IOException e){
				Log.getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
			}
			prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
			Log.getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		}
		
		try{
			Log.getLogger(null).info("Building JDA");
			final var jdaBuilder = new JDABuilder(AccountType.BOT).setToken(System.getProperty("RSN_TOKEN"));
			jdaBuilder.addEventListeners(new CommandsMessageListener());
			// jdaBuilder.addEventListeners(new OnlyImagesMessageListener());
			jdaBuilder.addEventListeners(new ShutdownListener());
			jdaBuilder.addEventListeners(new LogListener());
			jdaBuilder.addEventListeners(new AutoRolesListener());
			jdaBuilder.addEventListeners(new IdeaChannelMessageListener());
			jdaBuilder.addEventListeners(new ReactionListener());
			jdaBuilder.addEventListeners(new ReplyMessageListener());
			jdaBuilder.setAutoReconnect(true);
			jda = jdaBuilder.build();
			Log.setJDA(jda);
			jda.awaitReady();
			jda.getPresence().setActivity(Activity.playing("g?help for the help"));
			Log.getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(NewSettings::getConfiguration).count());
			Log.getLogger(null).info("Creating runners");
			final var scheduledRunners = List.of(new RemoveRolesScheduledRunner(jda), new AniListNotificationScheduledRunner(jda), new AniListMediaUserListScheduledRunner(jda), new SaveConfigScheduledRunner(), new DisplayDailyStatsScheduledRunner(jda), new OverwatchLeagueScheduledRunner(jda));
			for(final var scheduledRunner : scheduledRunners){
				executorService.scheduleAtFixedRate(scheduledRunner, scheduledRunner.getDelay(), scheduledRunner.getPeriod(), scheduledRunner.getPeriodUnit());
			}
			Log.getLogger(null).info("Started");
			announceStart();
		}
		catch(final LoginException | InterruptedException e){
			Log.getLogger(null).error("Couldn't start bot", e);
			System.exit(1);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Log.getLogger(null).info("Shutdown hook triggered");
			NewSettings.close();
		}));
		Log.getLogger(null).info("Shutdown hook registered");
		
		consoleHandler = new ConsoleHandler(jda);
		consoleHandler.start();
	}
	
	private static void announceStart(){
		Main.jda.getGuilds().stream().map(NewSettings::getConfiguration).map(GuildConfiguration::getAnnounceStartChannel).flatMap(Optional::stream).map(ChannelConfiguration::getChannel).flatMap(Optional::stream).forEach(c -> Actions.sendMessage(c, "Bot started :)"));
	}
	
	/**
	 * Close executors.
	 */
	public static void close(){
		QuizListener.stopAll();
		ReplyMessageListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchIRC.close();
		executorService.shutdownNow();
		consoleHandler.close();
		NewSettings.close();
	}
	
	@Nonnull
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
	 * Get the running JDA.
	 *
	 * @return The JDA.
	 */
	@Nonnull
	public static JDA getJDA(){
		return jda;
	}
	//https://api.overwatchleague.com/schedule
}
