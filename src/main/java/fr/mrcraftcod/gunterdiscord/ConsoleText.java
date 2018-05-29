package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.JDA;
import java.util.Scanner;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 29/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-29
 */
public class ConsoleText extends Thread
{
	private final JDA jda;
	
	public ConsoleText(JDA jda)
	{
		super();
		this.jda = jda;
		setDaemon(true);
		setName("Console watcher");
	}
	
	@Override
	public void run()
	{
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			String line = sc.nextLine();
			if(line == null)
				continue;
			if(line.startsWith("stop"))
				jda.shutdownNow();
		}
	}
}
