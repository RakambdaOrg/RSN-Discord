package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class AddRoleWrapper{
	private final Member member;
	private final Role role;
	private final AuditableRestAction<Void> action;
	
	public AddRoleWrapper(@NotNull Guild guild, @NotNull Member member, @NotNull Role role){
		this.member = member;
		this.role = role;
		action = guild.addRoleToMember(member, role);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Added role {} to {}", role, member));
	}
}
