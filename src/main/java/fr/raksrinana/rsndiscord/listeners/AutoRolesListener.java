package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RemoveRoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserRoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			final var now = LocalDateTime.now();
			Settings.get(event.getGuild()).getAutoRolesAndAddBackRoles(event.getMember()).stream().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(role -> Actions.giveRole(event.getMember(), role));
			Settings.get(event.getGuild()).getRemoveRoles().stream().filter(b -> Objects.equals(b.getUser().getUserId(), event.getUser().getIdLong())).filter(b -> b.getDate().isAfter(now)).map(RemoveRoleConfiguration::getRole).map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).forEach(r -> Actions.giveRole(event.getMember(), r));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
	
	@Override
	public void onGuildMemberLeave(@NonNull final GuildMemberLeaveEvent event){
		super.onGuildMemberLeave(event);
		try{
			Settings.get(event.getGuild()).getLeaverRole().flatMap(RoleConfiguration::getRole).ifPresent(role -> Settings.get(event.getGuild()).addAddBackRole(new UserRoleConfiguration(event.getUser(), role)));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user leave", e);
		}
	}
}
