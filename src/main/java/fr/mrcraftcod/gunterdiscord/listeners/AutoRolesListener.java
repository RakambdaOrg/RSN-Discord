package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.AutoRolesConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.Duration;
import java.util.Objects;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			Actions.giveRole(event.getUser(), new AutoRolesConfig(event.getGuild()).getAsList());
			final var currentTime = System.currentTimeMillis();
			final var config = new RemoveRoleConfig(event.getGuild());
			final var userGuildConfig = config.getValue(event.getUser().getIdLong());
			if(Objects.nonNull(userGuildConfig)){
				for(final var roleID : userGuildConfig.keySet()){
					final var diff = Duration.ofMillis(userGuildConfig.get(roleID) - currentTime);
					final var role = event.getGuild().getRoleById(roleID);
					if(!diff.isNegative()){
						Actions.giveRole(event.getGuild(), event.getUser(), role);
					}
				}
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
}
