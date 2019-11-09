package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 29/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-29
 */
class ConsoleHandler extends Thread{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleHandler.class);
	private static final int WAIT_DELAY = 10000;
	private final JDA jda;
	private boolean stop;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	ConsoleHandler(@Nonnull final JDA jda){
		super();
		this.jda = jda;
		this.stop = false;
		this.setDaemon(true);
		this.setName("Console watcher");
		LOGGER.info("Console handler created");
	}
	
	@Override
	public void run(){
		LOGGER.info("Console handler started");
		final var quitList = List.of("stop", "quit", "exit");
		try(final var sc = new Scanner(System.in)){
			while(!this.stop){
				try{
					if(!sc.hasNext()){
						try{
							Thread.sleep(WAIT_DELAY);
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
						if(Objects.nonNull(this.jda)){
							this.jda.shutdownNow();
						}
					}
					else if("leave".equalsIgnoreCase(arg1)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild as an argument");
						}
						else{
							final var guild = this.jda.getGuildById(args.poll());
							Objects.requireNonNull(guild).leave().queue();
							Log.getLogger(null).info("Guild {} left", guild);
						}
					}
					else if("gid".equalsIgnoreCase(arg1)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild as an argument");
						}
						else{
							final var guilds = this.jda.getGuildsByName(args.poll(), true);
							Log.getLogger(null).info("Guilds {} matcher", guilds);
						}
					}
					else if("listMembers".equalsIgnoreCase(arg1)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild as an argument");
						}
						else{
							final var guild = this.jda.getGuildById(args.poll());
							Log.getLogger(null).info("Members of {}: {}", guild, Objects.requireNonNull(guild).getMembers());
						}
					}
					else if("save".equalsIgnoreCase(arg1)){
						Settings.save();
					}
				}
				catch(final Exception e){
					Log.getLogger(null).warn("Error executing console command", e);
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
