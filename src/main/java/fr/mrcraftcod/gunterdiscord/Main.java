package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.listeners.*;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Main
{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	private static final String SETTINGS_NAME = "settings.json";
	private static final long SCHEDULED_DELAY = 60;
	private static final long SCHEDULED_PERIOD = 3600;
	private static JDA jda;
	private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(String[] args)
	{
		
		try
		{
			Settings.init(Paths.get(new File(SETTINGS_NAME).toURI()));
			jda = new JDABuilder(AccountType.BOT).setToken(System.getenv("GUNTER_TOKEN")).buildBlocking();
			jda.addEventListener(new CommandsMessageListener());
			// jda.addEventListener(new BannedRegexesMessageListener());
			jda.addEventListener(new OnlyImagesMessageListener());
			jda.addEventListener(new ShutdownListener());
			jda.addEventListener(new LogListener());
			jda.addEventListener(new AutoRolesListener());
			jda.addEventListener(new IdeaChannelMessageListener());
			jda.addEventListener(new QuestionReactionListener());
			jda.setAutoReconnect(true);
			jda.getPresence().setGame(Game.playing("g?help pour l'aide"));
			
			executorService.scheduleAtFixedRate(new ScheduledRunner(jda), SCHEDULED_DELAY, SCHEDULED_PERIOD, TimeUnit.SECONDS);
		}
		catch(IOException e)
		{
			Log.error(null, "Couldn't load settings", e);
		}
		catch(LoginException | InterruptedException e)
		{
			Log.error(null, "Couldn't start bot", e);
		}
		
		new ConsoleText(jda).start();
	}
	
	public static void close()
	{
		executorService.shutdownNow();
	}
	
	/**
	 * Get the running JDA.
	 *
	 * @return The JDA.
	 */
	public static JDA getJDA()
	{
		return jda;
	}
}
