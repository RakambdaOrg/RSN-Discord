package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class BanWrapper{
	private final Guild guild;
	private final Member member;
	private final int deletionDays;
	private final String reason;
	private final AuditableRestAction<Void> action;
	
	public BanWrapper(@NotNull Guild guild, @NotNull Member member, int deletionDays, @Nullable String reason){
		this.guild = guild;
		this.member = member;
		this.deletionDays = deletionDays;
		this.reason = reason;
		this.action = guild.ban(member, deletionDays, reason);
	}
	
	@NotNull
	public CompletableFuture<Void> sumbit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Banned {} deleting his messages for the last {} days with the reason: {}", member, deletionDays, reason));
	}
}
