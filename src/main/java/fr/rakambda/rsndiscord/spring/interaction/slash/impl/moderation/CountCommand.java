package fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@Component
public class CountCommand implements IExecutableSlashCommandGuild{
	@Override
	@NotNull
	public String getId(){
		return "count";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "mod/count";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply(true).submit();
		
		var channel = event.getGuildChannel();
		var counter = new AtomicLong(0);
		
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, "Counting...").submit())
				.thenCompose(msg -> JDAWrappers.history(channel)
						.foreachAsync(m -> {
							counter.incrementAndGet();
							return true;
						}))
				.thenCompose(empty -> JDAWrappers.message(channel, counter.get() + " messages").submit());
	}
}
