package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class RemoveRoleWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final Role role;
	
	public RemoveRoleWrapper(@NotNull Guild guild, @NotNull Member member, @NotNull Role role){
		super(guild.removeRoleFromMember(member, role));
		this.member = member;
		this.role = role;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Removed role {} from {}", role, member);
	}
}
