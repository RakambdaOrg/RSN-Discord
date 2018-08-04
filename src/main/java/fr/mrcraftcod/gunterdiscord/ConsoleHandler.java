package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 29/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-29
 */
public class ConsoleHandler extends Thread{
	private final JDA jda;
	private boolean stop;
	
	public ConsoleHandler(JDA jda){
		super();
		this.jda = jda;
		this.stop = false;
		setDaemon(true);
		setName("Console watcher");
	}
	
	@Override
	public void run(){
		try(Scanner sc = new Scanner(System.in)){
			while(!stop){
				String line = sc.nextLine();
				LinkedList<String> args = new LinkedList<>(Arrays.asList(line.split(" ")));
				if(args.isEmpty()){
					continue;
				}
				String arg1 = args.poll();
				if(arg1.equalsIgnoreCase("stop")){
					jda.shutdownNow();
				}
				else if(arg1.equalsIgnoreCase("leave")){
					if(args.isEmpty()){
						getLogger(null).warn("Please pass the guild as an argument");
					}
					else{
						Guild guild = jda.getGuildById(args.poll());
						guild.leave().queue();
						getLogger(null).info("Guild {} left", guild);
					}
				}
			}
		}
	}
	
	public void close(){
		this.stop = true;
	}
}
