package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AutoRolesListener extends ListenerAdapter{
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		try{
			Settings.get(event.getGuild()).getAutoRoles().stream()
					.flatMap(roleConfiguration -> roleConfiguration.getRole().stream())
					.forEach(role -> Actions.giveRole(event.getMember(), role));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error on user join", e);
		}
	}
}
