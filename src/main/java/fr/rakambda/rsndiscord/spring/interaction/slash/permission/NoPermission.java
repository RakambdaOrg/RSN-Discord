package fr.rakambda.rsndiscord.spring.interaction.slash.permission;

import net.dv8tion.jda.api.entities.User;
import org.jspecify.annotations.NonNull;

public record NoPermission() implements IPermission{
	@Override
	public boolean isAllowed(@NonNull User user){
		return true;
	}
}
