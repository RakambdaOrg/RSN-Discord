package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.done.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class RemoveRolesScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public RemoveRolesScheduledRunner(@Nonnull final JDA jda){
		getLogger(null).info("Creating roles runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting roles runner");
		final var currentTime = System.currentTimeMillis();
		for(final var guild : this.jda.getGuilds()){
			final var config = new RemoveRoleConfig(guild);
			getLogger(guild).debug("Processing guild {}", guild);
			config.getAsMap().ifPresent(guildConfig -> {
				for(final var userID : guildConfig.keySet()){
					final var member = guild.getMemberById(userID);
					getLogger(guild).debug("Processing user {}", member);
					final var userGuildConfig = guildConfig.get(userID);
					for(final var roleID : userGuildConfig.keySet()){
						final var diff = Duration.ofMillis(userGuildConfig.get(roleID) - currentTime);
						final var role = guild.getRoleById(roleID);
						getLogger(guild).debug("Processing role {}, diff is: {}", role, diff);
						if(diff.isNegative()){
							getLogger(guild).debug("Removed role for the user");
							Actions.removeRole(member, role);
							config.deleteKeyValue(userID, roleID);
						}
					}
				}
			});
		}
		getLogger(null).info("Roles runner done");
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
}
