package fr.raksrinana.rsndiscord.command.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public interface IPermission{
	boolean isGuildAllowed(@NotNull String id, @NotNull Member member);
	
	boolean isUserAllowed(@NotNull String id, @NotNull User user);
}
