package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModifyNicknameWrapper{
	private final Guild guild;
	private final Member target;
	private final String nickname;
	private final AuditableRestAction<Void> action;
	
	public ModifyNicknameWrapper(@NotNull Guild guild, @NotNull Member target, @Nullable String nickname){
		this.guild = guild;
		this.target = target;
		this.nickname = nickname;
		this.action = guild.modifyNickname(target, nickname);
	}
	
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Set nickname of {} to {}", target, nickname));
	}
}
