package fr.raksrinana.rsndiscord.interaction.command.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public record NoPermission() implements IPermission{
	public static final NoPermission INSTANCE = new NoPermission()
;
	@Override
	public boolean isGuildAllowed(@NotNull String id, @NotNull Member member){
		return true;
	}
	
	@Override
	public boolean isUserAllowed(@NotNull String id, @NotNull User user){
		return true;
	}
}
