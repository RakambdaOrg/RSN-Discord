package fr.raksrinana.rsndiscord.modules.autoroles.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.autoroles.config.LeaverRoles;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.Duration;
import java.util.Optional;

@EventListener
public class AutoRolesEventListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			Settings.get(event.getGuild()).getAutoRoles().stream()
					.flatMap(roleConfiguration -> roleConfiguration.getRole().stream())
					.forEach(role -> Actions.giveRole(event.getMember(), role));
			
			var leavingRolesConfiguration = Settings.get(event.getGuild()).getLeavingRolesConfiguration();
			leavingRolesConfiguration.getLeaver(event.getUser()).ifPresent(leaverRoles -> {
				leaverRoles.getRoles().stream()
						.map(RoleConfiguration::getRole)
						.map(Optional::get)
						.forEach(role -> Actions.giveRole(event.getMember(), role));
				leavingRolesConfiguration.removeUser(event.getUser());
			});
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
	
	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		Optional.ofNullable(event.getMember()).ifPresent(member -> {
			var leavingRolesConfiguration = Settings.get(event.getGuild()).getLeavingRolesConfiguration();
			leavingRolesConfiguration.addLeaver(new LeaverRoles(event.getUser(), member.getRoles()));
			
			Settings.get(event.getGuild())
					.getLeaveServerBanDuration()
					.ifPresent(banDuration -> event.getGuild()
							.retrieveBan(member.getUser())
							.submit()
							.thenApply(ban -> true)
							.exceptionally(exception -> false)
							.thenAccept(isBanned -> {
								if(!isBanned){
									Actions.softBan(event.getGuild().getDefaultChannel(), event.getJDA().getSelfUser(), member, "Left server", Duration.ofHours(1));
								}
							}));
		});
	}
}
