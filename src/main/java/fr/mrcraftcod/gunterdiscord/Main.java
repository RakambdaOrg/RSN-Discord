package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.listeners.*;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizMessageListener;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.LoggerFormatter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Main
{
	private static final String SETTINGS_NAME = "settings.json";
	private static final int WAIT_TIME = 3600;
	private static JDA jda;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(String[] args)
	{
		try
		{
			Log.setAppName("GunterDiscord");
			Handler handler = new FileHandler("log.log");
			handler.setFormatter(new LoggerFormatter());
			Log.getLogger().addHandler(handler);
		}
		catch(IOException e)
		{
			System.out.println("Error setting up logger");
			e.printStackTrace();
		}
		
		try
		{
			Settings.init(Paths.get(new File(SETTINGS_NAME).toURI()));
			jda = new JDABuilder(AccountType.BOT).setToken(System.getenv("GUNTER_TOKEN")).buildBlocking();
			jda.addEventListener(new CommandsMessageListener());
			// jda.addEventListener(new BannedRegexesMessageListener());
			jda.addEventListener(new OnlyImagesMessageListener());
			// jda.addEventListener(new OnlyQuestionsMessageListener());
			jda.addEventListener(new QuizMessageListener());
			jda.addEventListener(new ShutdownListener());
			jda.addEventListener(new HangmanListener());
			jda.addEventListener(new LogListener());
			jda.setAutoReconnect(true);
			jda.getPresence().setGame(Game.playing("Le chalumeau"));
			
			Thread t = new Thread(() -> {
				try
				{
					TextChannel trombiChannel = jda.getTextChannelById(new PhotoChannelConfig().getLong());
					//noinspection InfiniteLoopStatement
					while(true)
					{
						for(Message message : trombiChannel.getIterableHistory().cache(false))
							if(!message.isPinned())
								if(message.getCreationTime().isBefore(OffsetDateTime.now().minusDays(1)))
									message.delete().queue();
						
						Thread.sleep(WAIT_TIME * 1000);
					}
				}
				catch(InvalidClassException | NoValueDefinedException | InterruptedException e)
				{
					Log.error("Error getting photo channel", e);
				}
			});
			t.setDaemon(true);
			t.start();
		}
		catch(IOException e)
		{
			Log.error("Couldn't load settings", e);
		}
		catch(LoginException | InterruptedException e)
		{
			Log.error("Couldn't start bot", e);
		}
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
