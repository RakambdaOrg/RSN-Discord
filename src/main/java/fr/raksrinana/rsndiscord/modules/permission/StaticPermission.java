package fr.raksrinana.rsndiscord.modules.permission;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;

public class StaticPermission implements IPermission{
	private final boolean value;
	
	public StaticPermission(boolean value){
		this.value = value;
	}
	
	@Override
	public boolean isAllowed(@NonNull Member member){
		return value;
	}
}
