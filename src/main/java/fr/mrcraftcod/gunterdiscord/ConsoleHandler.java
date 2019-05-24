package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 29/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-29
 */
class ConsoleHandler extends Thread{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHandler.class);
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
		LOGGER.info("Console handler created");
	}
	
	@Override
	public void run(){
		LOGGER.info("Console handler started");
		final var quitList = List.of("stop", "quit", "exit");
		try(final var sc = new Scanner(System.in)){
			while(!stop){
				if(!sc.hasNext()){
					try{
						Thread.sleep(10000);
					}
					catch(final InterruptedException ignored){
					}
					continue;
				}
				final var line = sc.nextLine();
				final var args = new LinkedList<>(Arrays.asList(line.split(" ")));
				if(args.isEmpty()){
					continue;
				}
				final var arg1 = args.poll();
				if(quitList.contains(arg1)){
					Main.close();
					if(Objects.nonNull(jda)){
						jda.shutdownNow();
					}
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
						final var guilds = jda.getGuildsByName(args.poll(), true);
						getLogger(null).info("Guilds {} matcher", guilds);
					}
				}
				else if(arg1.equalsIgnoreCase("listMembers")){
					if(args.isEmpty()){
						getLogger(null).warn("Please pass the guild as an argument");
					}
					else{
						final var guild = jda.getGuildById(args.poll());
						getLogger(null).info("Members of {}: {}", guild, guild.getMembers());
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
