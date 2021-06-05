package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Activity;
import java.util.*;
import static net.dv8tion.jda.api.entities.Activity.ActivityType.DEFAULT;

@Log4j2
class ConsoleHandler extends Thread{
	private static final int WAIT_DELAY = 10000;
	private boolean stop;
	
	ConsoleHandler(){
		super();
		stop = false;
		setDaemon(true);
		setName("Console watcher");
		log.info("Console handler created");
	}
	
	@Override
	public void run(){
		log.info("Console handler started");
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
							log.warn("Please pass the guild as an argument");
						}
						else{
							var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId)).ifPresentOrElse(guild -> {
								JDAWrappers.leave(guild).submit();
							}, () -> log.warn("Guild with id {} not found", guildId));
						}
					}
					else if("gid".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							log.warn("Please pass the guild name as an argument");
						}
						else{
							var guilds = Main.getJda().getGuildsByName(args.pop(), true);
							log.info("Guilds matched : {}", guilds);
						}
					}
					else if("listMembers".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							log.warn("Please pass the guild id as an argument");
						}
						else{
							var guildId = args.poll();
							Optional.ofNullable(Main.getJda().getGuildById(guildId))
									.ifPresentOrElse(guild -> log.info("Members of {}: {}", guild, guild.getMembers()),
											() -> log.warn("Guild with id {} not found", guildId));
						}
					}
					else if("listGuilds".equalsIgnoreCase(command)){
						Main.getJda().getGuilds()
								.forEach(guild -> log.info("Guild ID:{}, name: {}, members:{}, owner:{}",
										guild.getId(),
										guild.getName(),
										guild.loadMembers().get().size(),
										guild.retrieveOwner().complete()));
					}
					else if("activity".equalsIgnoreCase(command)){
						if(args.isEmpty()){
							log.warn("Please pass the game");
						}
						else{
							JDAWrappers.editPresence().setActivity(Activity.of(DEFAULT, String.join(" ", args)));
						}
					}
					else if("save".equalsIgnoreCase(command)){
						Settings.save();
					}
				}
				catch(Exception e){
					log.warn("Error executing console command", e);
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
