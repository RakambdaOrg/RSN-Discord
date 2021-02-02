package fr.raksrinana.rsndiscord.permission;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public interface IPermission{
	boolean isAllowed(@NotNull Member member);
}
