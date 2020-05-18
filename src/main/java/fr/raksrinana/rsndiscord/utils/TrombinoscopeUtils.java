package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;

public class TrombinoscopeUtils{
	public static boolean isBanned(@NonNull Member member){
		return Settings.get(member.getGuild())
				.getTrombinoscope()
				.isUserBanned(member.getUser());
	}
	
	public static boolean isRegistered(@NonNull Member member){
		return Settings.get(member.getGuild()).getTrombinoscope()
				.getPosterRole()
				.flatMap(RoleConfiguration::getRole)
				.map(role -> member.getRoles().contains(role))
				.orElse(false);
	}
}
