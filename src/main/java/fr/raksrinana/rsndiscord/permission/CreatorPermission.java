package fr.raksrinana.rsndiscord.permission;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;

@NoArgsConstructor
public class CreatorPermission implements IPermission{
	@Override
	public boolean isAllowed(@NonNull Member member){
		return isModerator(member);
	}
}
