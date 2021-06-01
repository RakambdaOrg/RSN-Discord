package fr.raksrinana.rsndiscord.command2.permission;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

@NoArgsConstructor
public class CreatorPermission implements IPermission{
	public static final CreatorPermission CREATOR_PERMISSION = new CreatorPermission();

	@Override
	public boolean isAllowed(@NotNull String id, @NotNull Member member){
		return isCreator(member);
	}
}
