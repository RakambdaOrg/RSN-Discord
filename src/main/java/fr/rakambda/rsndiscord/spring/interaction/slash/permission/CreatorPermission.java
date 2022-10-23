package fr.rakambda.rsndiscord.spring.interaction.slash.permission;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

@NoArgsConstructor
public class CreatorPermission implements IPermission{
	private static final long MAIN_ACCOUNT = 170119951498084352L;
	private static final long SECONDARY_ACCOUNT = 432628353024131085L;
	private static final Set<Long> ACCOUNTS = Set.of(MAIN_ACCOUNT, SECONDARY_ACCOUNT);
	
	@Override
	public boolean isAllowed(@NotNull String id, @NotNull User user){
		return ACCOUNTS.contains(user.getIdLong());
	}
}
