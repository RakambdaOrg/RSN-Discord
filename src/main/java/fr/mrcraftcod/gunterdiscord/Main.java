package fr.mrcraftcod.gunterdiscord;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import fr.mrcraftcod.gunterdiscord.listeners.*;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.runners.DisplayDailyStatsScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.OverwatchLeagueScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.RemoveRolesScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.SaveConfigScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListMediaUserListScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListNotificationScheduledRunner;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.irc.TwitchIRC;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
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
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

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
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(@Nonnull final String[] args){
		getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		final var parameters = new CLIParameters();
		try{
			JCommander.newBuilder().addObject(parameters).build().parse(args);
		}
		catch(final ParameterException e){
			getLogger(null).error("Failed to parse arguments", e);
			e.usage();
			return;
		}
		
		if(Objects.nonNull(parameters.getConfigurationFile())){
			final var prop = new Properties();
			try(final var is = new FileInputStream(parameters.getConfigurationFile())){
				prop.load(is);
			}
			catch(final IOException e){
				getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
			}
			prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
			getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		}
		
		try{
			getLogger(null).info("Building JDA");
			final var jdaBuilder = new JDABuilder(AccountType.BOT).setToken(System.getProperty("RSN_TOKEN"));
			jdaBuilder.addEventListeners(new CommandsMessageListener());
			// jdaBuilder.addEventListeners(new OnlyImagesMessageListener());
			jdaBuilder.addEventListeners(new ShutdownListener());
			jdaBuilder.addEventListeners(new LogListener());
			jdaBuilder.addEventListeners(new AutoRolesListener());
			jdaBuilder.addEventListeners(new IdeaChannelMessageListener());
			jdaBuilder.addEventListeners(new QuestionReactionListener());
			jdaBuilder.addEventListeners(new ReplyMessageListener());
			jdaBuilder.setAutoReconnect(true);
			jda = jdaBuilder.build();
			Log.setJDA(jda);
			jda.awaitReady();
			jda.getPresence().setActivity(Activity.playing("g?help for the help"));
			getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(NewSettings::getConfiguration).count());
			getLogger(null).info("Creating runners");
			final var scheduledRunners = List.of(new RemoveRolesScheduledRunner(jda), new AniListNotificationScheduledRunner(jda), new AniListMediaUserListScheduledRunner(jda), new SaveConfigScheduledRunner(), new DisplayDailyStatsScheduledRunner(jda), new OverwatchLeagueScheduledRunner(jda));
			for(final var scheduledRunner : scheduledRunners){
				executorService.scheduleAtFixedRate(scheduledRunner, scheduledRunner.getDelay(), scheduledRunner.getPeriod(), scheduledRunner.getPeriodUnit());
			}
			getLogger(null).info("Started");
		}
		catch(final LoginException | InterruptedException e){
			getLogger(null).error("Couldn't start bot", e);
			System.exit(1);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			getLogger(null).info("Shutdown hook triggered");
			NewSettings.close();
		}));
		getLogger(null).info("Shutdown hook registered");
		
		consoleHandler = new ConsoleHandler(jda);
		consoleHandler.start();
	}
	
	/**
	 * Close executors.
	 */
	public static void close(){
		QuizListener.stopAll();
		ReplyMessageListener.stopAll();
		GunterAudioManager.stopAll();
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
		catch(final IOException e){
			getLogger(null).warn("Error reading version jsonConfigFile", e);
		}
		return properties.getProperty("simulator.version", "Unknown");
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
