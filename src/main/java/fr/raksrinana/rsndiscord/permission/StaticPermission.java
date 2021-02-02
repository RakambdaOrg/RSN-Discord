package fr.raksrinana.rsndiscord.permission;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class StaticPermission implements IPermission{
	private final boolean value;
	
	public StaticPermission(boolean value){
		this.value = value;
	}
	
	@Override
	public boolean isAllowed(@NotNull Member member){
		return value;
	}
}
