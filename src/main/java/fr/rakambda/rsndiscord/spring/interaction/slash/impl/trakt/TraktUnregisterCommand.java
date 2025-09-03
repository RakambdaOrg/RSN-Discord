package fr.rakambda.rsndiscord.spring.interaction.slash.impl.trakt;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.repository.TraktRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class TraktUnregisterCommand implements IExecutableSlashCommandGuild{
	private final TraktRepository traktRepository;
	
	@Autowired
	public TraktUnregisterCommand(TraktRepository traktRepository){
		this.traktRepository = traktRepository;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "unregister";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "trakt/unregister";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		traktRepository.findById(member.getIdLong())
				.ifPresent(entity -> {
					entity.setEnabled(false);
					traktRepository.save(entity);
				});
		return JDAWrappers.reply(event, "Done")
				.ephemeral(true)
				.submit();
	}
}
