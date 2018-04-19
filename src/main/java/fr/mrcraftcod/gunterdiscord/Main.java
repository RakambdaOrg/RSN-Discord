package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.listeners.CommandsMessageListener;
import fr.mrcraftcod.gunterdiscord.listeners.ReadyListener;
import fr.mrcraftcod.gunterdiscord.listeners.ShutdownListener;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Main
{
	private static final String SETTINGS_NAME = "settings.json";
	private static JDA jda;
	
	public static void main(String[] args) throws LoginException, InterruptedException, IOException
	{
		Settings.init(Paths.get(new File(SETTINGS_NAME).toURI()));
		
		jda = new JDABuilder(AccountType.BOT).setToken(System.getenv("GUNTER_TOKEN")).buildBlocking();
		jda.addEventListener(new ReadyListener());
		jda.addEventListener(new CommandsMessageListener());
		// jda.addEventListener(new BannedRegexesMessageListener());
		// jda.addEventListener(new OnlyImagesMessageListener());
		// jda.addEventListener(new OnlyQuestionsMessageListener());
		//jda.addEventListener(new QuizMessageListener());
		jda.addEventListener(new ShutdownListener());
		jda.setAutoReconnect(true);
		jda.getPresence().setGame(Game.playing("Le chalumeau"));
	}
	
	public static JDA getJDA()
	{
		return jda;
	}
}
