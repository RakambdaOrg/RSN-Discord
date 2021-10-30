package fr.raksrinana.rsndiscord.command.permission;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

@NoArgsConstructor
public class CreatorPermission implements IPermission{
	public static final CreatorPermission CREATOR_PERMISSION = new CreatorPermission();

	@Override
	public boolean isGuildAllowed(@NotNull String id, @NotNull Member member){
		return isCreator(member);
	}
	
	@Override
	public boolean isUserAllowed(@NotNull String id, @NotNull User user){
		return isCreator(user);
	}
}
