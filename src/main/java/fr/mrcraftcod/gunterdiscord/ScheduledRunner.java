package fr.mrcraftcod.gunterdiscord;

import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import java.util.Map;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class ScheduledRunner implements Runnable
{
	private final JDA jda;
	
	public ScheduledRunner(JDA jda)
	{
		this.jda = jda;
	}
	
	@Override
	public void run()
	{
		long currentTime = System.currentTimeMillis();
		RemoveRoleConfig config = new RemoveRoleConfig();
		for(Guild guild : jda.getGuilds())
		{
			Map<Long, Map<Long, Long>> guildConfig = config.getAsMap(guild);
			for(Long userID : guildConfig.keySet())
			{
				Member member = guild.getMemberById(userID);
				Map<Long, Long> userGuildConfig = guildConfig.get(userID);
				for(Long roleID : userGuildConfig.keySet())
				{
					if(currentTime >= userGuildConfig.get(roleID))
					{
						Actions.removeRole(member, guild.getRoleById(roleID));
					}
				}
			}
		}
	}
}
