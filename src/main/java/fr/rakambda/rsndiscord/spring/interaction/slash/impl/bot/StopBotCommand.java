package fr.rakambda.rsndiscord.spring.interaction.slash.impl.bot;

import fr.rakambda.rsndiscord.spring.StopService;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.CreatorPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class StopBotCommand implements IExecutableSlashCommandGuild, IExecutableSlashCommandUser{
	private final StopService stopService;
	
	@Autowired
	public StopBotCommand(StopService stopService){
		this.stopService = stopService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "stop";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "bot/stop";
	}
	
	@Override
	@NonNull
	public IPermission getPermission(){
		return new CreatorPermission();
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		return execute(event);
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeUser(@NonNull SlashCommandInteraction event){
		return execute(event);
	}
	
	@NonNull
	private CompletableFuture<Message> execute(@NonNull SlashCommandInteraction event){
		return event.deferReply(true).submit()
				.thenCompose(empty -> JDAWrappers.reply(event, "Stopping").ephemeral(true).submit())
				.thenCompose(empty -> {
					try{
						stopService.shutdown(event.getJDA());
						return CompletableFuture.completedFuture(null);
					}
					catch(InterruptedException | ExecutionException | TimeoutException e){
						throw new RuntimeException("Failed to stop bot", e);
					}
				});
	}
}
