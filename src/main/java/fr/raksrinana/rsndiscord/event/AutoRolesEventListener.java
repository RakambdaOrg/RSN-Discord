package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.autoroles.LeaverRoles;
import fr.raksrinana.rsndiscord.settings.guild.schedule.UnbanScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.ZonedDateTime;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.addSchedule;
import static java.util.Optional.ofNullable;

@EventListener
public class AutoRolesEventListener extends ListenerAdapter{
	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		
		var guild = event.getGuild();
		var user = event.getUser();
		ofNullable(event.getMember()).ifPresent(member -> {
			var leavingRolesConfiguration = Settings.get(guild).getLeavingRolesConfiguration();
			leavingRolesConfiguration.addLeaver(new LeaverRoles(user, member.getRoles()));
			
			Settings.get(guild).getLeaveServerBanDuration()
					.ifPresent(banDuration -> guild.retrieveBan(user).submit()
							.thenApply(ban -> true)
							.exceptionally(exception -> false)
							.thenAccept(isBanned -> {
								if(!isBanned){
									var unbanScheduleConfiguration = new UnbanScheduleConfiguration(event.getJDA().getSelfUser(),
											guild.getDefaultChannel(),
											ZonedDateTime.now().plus(banDuration),
											"Banned for: " + "Left server",
											member.getId());
									addSchedule(guild, unbanScheduleConfiguration);
									
									member.ban(0, "Left server").submit();
								}
							}));
		});
	}
	
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		
		var guild = event.getGuild();
		try{
			var member = event.getMember();
			var user = event.getUser();
			
			Settings.get(guild).getAutoRoles().stream()
					.flatMap(roleConfiguration -> roleConfiguration.getRole().stream())
					.forEach(role -> guild.addRoleToMember(member, role).submit());
			
			var leavingRolesConfiguration = Settings.get(guild).getLeavingRolesConfiguration();
			leavingRolesConfiguration.getLeaver(user).ifPresent(leaverRoles -> {
				leaverRoles.getRoles().stream()
						.map(RoleConfiguration::getRole)
						.flatMap(Optional::stream)
						.forEach(role -> guild.addRoleToMember(member, role).submit());
				leavingRolesConfiguration.removeUser(user);
			});
		}
		catch(final Exception e){
			Log.getLogger(guild).error("Error on user join", e);
		}
	}
}
