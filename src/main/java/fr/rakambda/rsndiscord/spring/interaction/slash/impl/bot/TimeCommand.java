package fr.rakambda.rsndiscord.spring.interaction.slash.impl.bot;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import static java.awt.Color.GREEN;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

@Slf4j
@Component
public class TimeCommand implements IExecutableSlashCommandGuild, IExecutableSlashCommandUser{
	@Override
	@NonNull
	public String getId(){
		return "time";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "bot/time";
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
		var embed = new EmbedBuilder()
				.setColor(GREEN)
				.setTitle("Server time")
				.addField("Time", ZonedDateTime.now().format(ISO_ZONED_DATE_TIME), false)
				.addField("Milliseconds", String.valueOf(System.currentTimeMillis()), false)
				.build();
		
		return JDAWrappers.reply(event, embed).ephemeral(true).submit();
	}
}
