package fr.raksrinana.rsndiscord.permission;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public record StaticPermission(boolean value) implements IPermission{
	@Override
	public boolean isAllowed(@NotNull Member member){
		return value;
	}
}
