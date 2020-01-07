package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.log.Log;
import java.util.*;

/**
 * Handles commands sent in the standard input.
 */
class ConsoleHandler extends Thread{
	private static final int WAIT_DELAY = 10000;
	private boolean stop;
	
	ConsoleHandler(){
		super();
		this.stop = false;
		this.setDaemon(true);
		this.setName("Console watcher");
		Log.getLogger(null).info("Console handler created");
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Console handler started");
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
					final var command = args.poll();
					if(quitList.contains(command)){
						Main.close();
					}
					else if("leave".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild as an argument");
						}
						else{
							final var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId)).ifPresentOrElse(guild -> {
								guild.leave().queue();
								Log.getLogger(guild).info("Guild {} left", guild);
							}, () -> Log.getLogger(null).warn("Guild with id {} not found", guildId));
						}
					}
					else if("gid".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild name as an argument");
						}
						else{
							final var guilds = Main.getJda().getGuildsByName(args.pop(), true);
							Log.getLogger(null).info("Guilds matched : {}", guilds);
						}
					}
					else if("listMembers".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger(null).warn("Please pass the guild id as an argument");
						}
						else{
							final var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId)).ifPresentOrElse(guild -> Log.getLogger(guild).info("Members of {}: {}", guild, guild.getMembers()), () -> Log.getLogger(null).warn("Guild with id {} not found", guildId));
						}
					}
					else if("save".equalsIgnoreCase(command)){
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
