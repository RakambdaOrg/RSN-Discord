package fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.scheduledevents.ScheduledEventsService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RegenerateEventsCommand implements IExecutableSlashCommandGuild{
	private final ScheduledEventsService scheduledEventsService;
	
	@Autowired
	public RegenerateEventsCommand(ScheduledEventsService scheduledEventsService){
		this.scheduledEventsService = scheduledEventsService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "regenerate-events";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "mod/regenerate-events";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply(true).submit();
		scheduledEventsService.update(guild);
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, "Ok").submit());
	}
}
