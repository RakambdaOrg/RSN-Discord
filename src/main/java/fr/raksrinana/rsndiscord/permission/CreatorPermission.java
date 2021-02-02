package fr.raksrinana.rsndiscord.permission;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.Utilities.isModerator;

@NoArgsConstructor
public class CreatorPermission implements IPermission{
	@Override
	public boolean isAllowed(@NotNull Member member){
		return isModerator(member);
	}
}
