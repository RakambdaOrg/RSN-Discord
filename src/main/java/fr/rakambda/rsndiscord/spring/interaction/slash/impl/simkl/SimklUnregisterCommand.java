package fr.rakambda.rsndiscord.spring.interaction.slash.impl.simkl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.repository.SimklRepository;
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
public class SimklUnregisterCommand implements IExecutableSlashCommandGuild{
	private final SimklRepository simklRepository;
	
	@Autowired
	public SimklUnregisterCommand(SimklRepository simklRepository){
		this.simklRepository = simklRepository;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "unregister";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "simkl/unregister";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		simklRepository.findById(member.getIdLong())
				.ifPresent(entity -> {
					entity.setEnabled(false);
					simklRepository.save(entity);
				});
		return JDAWrappers.reply(event, "Done")
				.ephemeral(true)
				.submit();
	}
}
