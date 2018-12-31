package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.listeners.*;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
import fr.mrcraftcod.gunterdiscord.runners.DisplayDailyStatsScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.RemoveRolesScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.SaveConfigScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListMediaUserListScheduledRunner;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListNotificationScheduledRunner;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
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
	private static final String SETTINGS_NAME = "settings.json";
	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private static JDA jda;
	private static ConsoleHandler consoleHandler;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(final String[] args){
		try{
			Settings.init(Paths.get(new File(SETTINGS_NAME).toURI()));
			jda = new JDABuilder(AccountType.BOT).setToken(System.getenv("GUNTER_TOKEN")).build();
			jda.awaitReady();
			jda.addEventListener(new CommandsMessageListener());
			// jda.addEventListener(new OnlyImagesMessageListener());
			jda.addEventListener(new ShutdownListener());
			jda.addEventListener(new LogListener());
			jda.addEventListener(new AutoRolesListener());
			jda.addEventListener(new IdeaChannelMessageListener());
			jda.addEventListener(new QuestionReactionListener());
			jda.setAutoReconnect(true);
			jda.getPresence().setGame(Game.playing("g?help for the help"));
			
			final var scheduledRunners = List.of(new RemoveRolesScheduledRunner(jda), new AniListNotificationScheduledRunner(jda), new AniListMediaUserListScheduledRunner(jda), new SaveConfigScheduledRunner(), new DisplayDailyStatsScheduledRunner(jda));
			for(final var scheduledRunner : scheduledRunners){
				executorService.scheduleAtFixedRate(scheduledRunner, scheduledRunner.getDelay(), scheduledRunner.getPeriod(), scheduledRunner.getPeriodUnit());
			}
		}
		catch(final IOException e){
			getLogger(null).error("Couldn't load settings", e);
		}
		catch(final LoginException | InterruptedException e){
			getLogger(null).error("Couldn't start bot", e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try{
				Settings.save();
			}
			catch(final IOException e){
				getLogger(null).error("Error saving settings", e);
			}
			Settings.close();
		}));
		
		consoleHandler = new ConsoleHandler(jda);
		consoleHandler.start();
	}
	
	/**
	 * Close executors.
	 */
	public static void close(){
		QuizListener.stopAll();
		GunterAudioManager.stopAll();
		
		executorService.shutdownNow();
		consoleHandler.close();
		
		try{
			Settings.save();
		}
		catch(final IOException e){
			Log.getLogger(null).error("Error saving configuration", e);
		}
		Settings.close();
	}
	
	/**
	 * Get the running JDA.
	 *
	 * @return The JDA.
	 */
	public static JDA getJDA(){
		return jda;
	}
}
