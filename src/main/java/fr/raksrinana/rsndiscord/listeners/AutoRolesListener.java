package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.schedule.RemoveRoleScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;
import java.util.Optional;

public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			Settings.get(event.getGuild()).getAutoRolesAndAddBackRoles(event.getMember()).stream().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(role -> Actions.giveRole(event.getMember(), role));
			Settings.get(event.getGuild()).getSchedules().stream().filter(scheduleConfiguration -> Objects.equals(scheduleConfiguration.getTag(), ScheduleTag.REMOVE_ROLE) && Objects.equals(event.getMember().getIdLong(), scheduleConfiguration.getUser().getUserId())).flatMap(scheduleConfiguration -> RemoveRoleScheduleHandler.getRole(scheduleConfiguration).stream()).forEach(r -> Actions.giveRole(event.getMember(), r));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
}
