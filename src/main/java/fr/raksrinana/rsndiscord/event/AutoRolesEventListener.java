package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.schedule.impl.UnbanMemberScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.autoroles.LeaverRoles;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@EventListener
@Log4j2
public class AutoRolesEventListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		
		var guild = event.getGuild();
		var user = event.getUser();
		
		try(var ignored = LogContext.with(guild).with(user)){
			var member = event.getMember();
			
			Settings.get(guild).getAutoRoles().stream()
					.flatMap(roleConfiguration -> roleConfiguration.getRole().stream())
					.forEach(role -> JDAWrappers.addRole(member, role).submit());
			
			var leavingRolesConfiguration = Settings.get(guild).getLeavingRolesConfiguration();
			leavingRolesConfiguration.getLeaver(user).ifPresent(leaverRoles -> {
				leaverRoles.getRoles().stream()
						.map(RoleConfiguration::getRole)
						.flatMap(Optional::stream)
						.forEach(role -> JDAWrappers.addRole(member, role).submit());
				leavingRolesConfiguration.removeUser(user);
			});
		}
		catch(Exception e){
			log.error("Error on user join", e);
		}
	}
	
	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		
		var guild = event.getGuild();
		var user = event.getUser();
		
		try(var ignored = LogContext.with(guild).with(user)){
			var guildSettings = Settings.get(guild);
			
			ofNullable(event.getMember()).ifPresent(member -> {
				var leavingRolesConfiguration = guildSettings.getLeavingRolesConfiguration();
				leavingRolesConfiguration.addLeaver(new LeaverRoles(user, member.getRoles()));
				
				guildSettings.getLeaveServerBanDuration()
						.ifPresent(banDuration -> guild.retrieveBan(user).submit()
								.thenApply(ban -> true)
								.exceptionally(exception -> false)
								.thenAccept(isBanned -> {
									if(!isBanned){
										guildSettings.add(new UnbanMemberScheduleHandler(member.getIdLong(), ZonedDateTime.now().plus(banDuration)));
										JDAWrappers.ban(member, 0, "Left server").submit();
									}
								}));
			});
		}
	}
}
