package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import java.time.Duration;
import java.util.Map;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class ScheduledRunner implements Runnable{
	private final JDA jda;
	
	public ScheduledRunner(JDA jda){
		getLogger(null).info("Creating scheduled runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting scheduled runner");
		long currentTime = System.currentTimeMillis();
		RemoveRoleConfig config = new RemoveRoleConfig();
		for(Guild guild : jda.getGuilds()){
			getLogger(guild).info("Processing guild {}", guild);
			Map<Long, Map<Long, Long>> guildConfig = config.getAsMap(guild);
			for(Long userID : guildConfig.keySet()){
				Member member = guild.getMemberById(userID);
				getLogger(guild).info("Processing user {}", member);
				Map<Long, Long> userGuildConfig = guildConfig.get(userID);
				for(Long roleID : userGuildConfig.keySet()){
					Duration diff = Duration.ofMillis(userGuildConfig.get(roleID) - currentTime);
					Role role = guild.getRoleById(roleID);
					getLogger(guild).info("Processing role {}, diff is: {}", role, diff);
					if(diff.isNegative()){
						Actions.removeRole(member, role);
						config.deleteKeyValue(guild, userID, roleID);
					}
				}
			}
		}
	}
}
