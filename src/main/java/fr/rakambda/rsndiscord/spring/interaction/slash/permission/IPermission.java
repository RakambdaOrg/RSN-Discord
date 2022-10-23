package fr.rakambda.rsndiscord.spring.interaction.slash.permission;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public interface IPermission{
	boolean isAllowed(@NotNull String id, @NotNull User user);
}
