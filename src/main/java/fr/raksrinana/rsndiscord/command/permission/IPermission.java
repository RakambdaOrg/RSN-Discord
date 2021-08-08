package fr.raksrinana.rsndiscord.command.permission;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public interface IPermission{
	boolean isAllowed(@NotNull String id, @NotNull Member member);
}
