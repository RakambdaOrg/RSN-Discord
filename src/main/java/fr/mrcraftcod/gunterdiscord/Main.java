package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Main
{
	private static final String SETTINGS_NAME = "settings.properties";
	
	public static void main(String[] args) throws LoginException, InterruptedException, FileNotFoundException
	{
		Settings settings = new Settings(new File(SETTINGS_NAME));
		
		JDA jda = new JDABuilder(AccountType.BOT).setToken("***REMOVED***").addEventListener(new ReadyListener()).buildBlocking();
		jda.addEventListener(new MessageListener(settings));
		jda.addEventListener(new ShutdownListener(settings));
		jda.setAutoReconnect(true);
	}
}
