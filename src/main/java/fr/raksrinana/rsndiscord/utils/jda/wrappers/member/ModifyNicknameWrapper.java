package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class ModifyNicknameWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member target;
	private final String nickname;
	
	public ModifyNicknameWrapper(@NotNull Guild guild, @NotNull Member target, @Nullable String nickname){
		super(guild.modifyNickname(target, nickname));
		this.target = target;
		this.nickname = nickname;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Set nickname of {} to {}", target, nickname);
	}
}
