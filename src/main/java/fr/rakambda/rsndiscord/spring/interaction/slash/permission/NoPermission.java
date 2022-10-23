package fr.rakambda.rsndiscord.spring.interaction.slash.permission;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public record NoPermission() implements IPermission{
	@Override
	public boolean isAllowed(@NotNull String id, @NotNull User user){
		return true;
	}
}
