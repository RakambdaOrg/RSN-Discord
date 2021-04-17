package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Activity;
import java.util.*;
import static net.dv8tion.jda.api.entities.Activity.ActivityType.DEFAULT;

/**
 * Handles commands sent in the standard input.
 */
class ConsoleHandler extends Thread{
	private static final int WAIT_DELAY = 10000;
	private boolean stop;
	
	ConsoleHandler(){
		super();
		stop = false;
		setDaemon(true);
		setName("Console watcher");
		Log.getLogger().info("Console handler created");
	}
	
	@Override
	public void run(){
		Log.getLogger().info("Console handler started");
		var quitList = List.of("stop", "quit", "exit");
		try(var sc = new Scanner(System.in)){
			while(!stop){
				try{
					if(!sc.hasNext()){
						try{
							Thread.sleep(WAIT_DELAY);
						}
						catch(InterruptedException ignored){
						}
						continue;
					}
					var line = sc.nextLine();
					var args = new LinkedList<>(Arrays.asList(line.split(" ")));
					if(args.isEmpty()){
						continue;
					}
					var command = args.poll();
					if(quitList.contains(command)){
						Main.close();
					}
					else if("leave".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger().warn("Please pass the guild as an argument");
						}
						else{
							var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId)).ifPresentOrElse(guild -> {
								guild.leave().submit();
								Log.getLogger(guild).info("Guild {} left", guild);
							}, () -> Log.getLogger().warn("Guild with id {} not found", guildId));
						}
					}
					else if("gid".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger().warn("Please pass the guild name as an argument");
						}
						else{
							var guilds = Main.getJda().getGuildsByName(args.pop(), true);
							Log.getLogger().info("Guilds matched : {}", guilds);
						}
					}
					else if("listMembers".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger().warn("Please pass the guild id as an argument");
						}
						else{
							var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId))
									.ifPresentOrElse(guild -> Log.getLogger(guild).info("Members of {}: {}", guild, guild.getMembers()),
											() -> Log.getLogger().warn("Guild with id {} not found", guildId));
						}
					}
					else if("listGuilds".equalsIgnoreCase(command)){
						Main.getJda().getGuilds()
								.forEach(guild -> Log.getLogger(guild).info("Guild ID:{}, name: {}, members:{}, owner:{}",
										guild.getId(),
										guild.getName(),
										guild.loadMembers().get().size(),
										guild.retrieveOwner().complete()));
					}
					else if("game".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							Log.getLogger().warn("Please pass the game");
						}
						else{
							Main.getJda().getPresence().setActivity(Activity.of(DEFAULT, String.join(" ", args)));
						}
					}
					else if("save".equalsIgnoreCase(command)){
						Settings.save();
					}
				}
				catch(Exception e){
					Log.getLogger().warn("Error executing console command", e);
				}
			}
		}
	}
	
	/**
	 * Close the console handler.
	 */
	void close(){
		stop = true;
	}
}
