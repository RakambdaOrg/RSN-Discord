package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class RemoveRoleWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final Role role;
	
	public RemoveRoleWrapper(@NonNull Guild guild, @NonNull Member member, @NonNull Role role){
		super(guild.removeRoleFromMember(member, role));
		this.member = member;
		this.role = role;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Removed role {} from {}", role, member);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to remove role {} from user {}", role, member, throwable);
	}
}
