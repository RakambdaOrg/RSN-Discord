package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.JDA;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 29/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-29
 */
class ConsoleHandler extends Thread{
	private final JDA jda;
	private boolean stop;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	ConsoleHandler(final JDA jda){
		super();
		this.jda = jda;
		this.stop = false;
		setDaemon(true);
		setName("Console watcher");
	}
	
	@Override
	public void run(){
		final var quitList = List.of("stop", "quit");
		try(final var sc = new Scanner(System.in)){
			while(!stop){
				final var line = sc.nextLine();
				final var args = new LinkedList<>(Arrays.asList(line.split(" ")));
				if(args.isEmpty()){
					continue;
				}
				final var arg1 = args.poll();
				if(quitList.contains(arg1)){
					Main.close();
					jda.shutdownNow();
				}
				else if(arg1.equalsIgnoreCase("leave")){
					if(args.isEmpty()){
						getLogger(null).warn("Please pass the guild as an argument");
					}
					else{
						final var guild = jda.getGuildById(args.poll());
						guild.leave().queue();
						getLogger(null).info("Guild {} left", guild);
					}
				}
				else if(arg1.equalsIgnoreCase("gid")){
					if(args.isEmpty()){
						getLogger(null).warn("Please pass the guild as an argument");
					}
					else{
						final var guild = jda.getGuildsByName(args.poll(), true);
						getLogger(null).info("Guild {} left", guild);
					}
				}
			}
		}
	}
	
	/**
	 * Close the console handler.
	 */
	void close(){
		this.stop = true;
	}
}
