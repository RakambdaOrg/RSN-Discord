package fr.raksrinana.rsndiscord.permission;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;

public interface IPermission{
	boolean isAllowed(@NonNull Member member);
}
