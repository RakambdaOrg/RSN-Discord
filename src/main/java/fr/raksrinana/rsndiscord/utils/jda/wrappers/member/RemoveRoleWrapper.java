package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class RemoveRoleWrapper{
	private final Guild guild;
	private final Member member;
	private final Role role;
	private final AuditableRestAction<Void> action;
	
	public RemoveRoleWrapper(@NotNull Guild guild, @NotNull Member member, @NotNull Role role){
		this.guild = guild;
		this.member = member;
		this.role = role;
		this.action = guild.removeRoleFromMember(member, role);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Removed role {} from {}", role, member));
	}
}
