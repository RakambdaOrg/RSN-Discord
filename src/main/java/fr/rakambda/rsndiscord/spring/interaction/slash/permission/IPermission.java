package fr.rakambda.rsndiscord.spring.interaction.slash.permission;

import net.dv8tion.jda.api.entities.User;
import org.jspecify.annotations.NonNull;

public interface IPermission{
	boolean isAllowed(@NonNull User user);
}
