package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.listeners.MessageListener;
import fr.mrcraftcod.gunterdiscord.listeners.ReadyListener;
import fr.mrcraftcod.gunterdiscord.listeners.ShutdownListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
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
	
	public static void main(String[] args) throws LoginException, InterruptedException, IOException
	{
		Settings settings = new Settings(Paths.get(new File(SETTINGS_NAME).toURI()));
		
		JDA jda = new JDABuilder(AccountType.BOT).setToken(System.getenv("GUNTER_TOKEN")).buildBlocking();
		jda.addEventListener(new ReadyListener());
		jda.addEventListener(new MessageListener(settings));
		jda.addEventListener(new ShutdownListener(settings));
		jda.setAutoReconnect(true);
	}
}
