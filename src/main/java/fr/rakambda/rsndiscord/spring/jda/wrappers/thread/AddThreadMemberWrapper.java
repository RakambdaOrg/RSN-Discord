package fr.rakambda.rsndiscord.spring.jda.wrappers.thread;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class AddThreadMemberWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final ThreadChannel thread;
	private final Object target;
	
	public AddThreadMemberWrapper(@NotNull ThreadChannel thread, @NotNull User user){
		super(thread.addThreadMember(user));
		this.thread = thread;
		target = user;
	}
	
	public AddThreadMemberWrapper(@NotNull ThreadChannel thread, @NotNull Member member){
		super(thread.addThreadMember(member));
		this.thread = thread;
		target = member;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Added {} to thread {}", target, thread);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to add user {} to thread {}", target, thread, throwable);
	}
}
