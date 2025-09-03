package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class ModifyNicknameWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final String nickname;
	
	public ModifyNicknameWrapper(@NonNull Guild guild, @NonNull Member member, @Nullable String nickname){
		super(guild.modifyNickname(member, nickname));
		this.member = member;
		this.nickname = nickname;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Set nickname of {} to {}", member, nickname);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to modify nickname for user {}", member, throwable);
	}
}
