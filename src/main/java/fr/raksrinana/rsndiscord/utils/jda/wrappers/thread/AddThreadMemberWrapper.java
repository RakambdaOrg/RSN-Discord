package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class AddThreadMemberWrapper{
	private final ThreadChannel thread;
	private final Object target;
	private final RestAction<Void> action;
	
	public AddThreadMemberWrapper(@NotNull ThreadChannel thread, @NotNull User user){
		this.thread = thread;
		target = user;
		action = thread.addThreadMember(user);
	}
	
	public AddThreadMemberWrapper(@NotNull ThreadChannel thread, @NotNull Member member){
		this.thread = thread;
		target = member;
		action = thread.addThreadMember(member);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Added {} to thread {}", target, thread));
	}
}
