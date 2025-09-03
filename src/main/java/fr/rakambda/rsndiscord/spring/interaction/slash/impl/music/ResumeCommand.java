package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class ResumeCommand implements IExecutableSlashCommandGuild{
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public ResumeCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService, RabbitService rabbitService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "resume";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "music/resume";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		var deferred = event.deferReply().submit();
		
		var result = audioServiceFactory.get(guild).getTrackScheduler().pause(false);
		var message = result ? "music.resumed" : "music.nothing-playing";
		var content = localizationService.translate(event.getUserLocale(), message, event.getUser().getAsMention());
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, content).submitAndDelete(5, rabbitService));
	}
}
