package fr.rakambda.rsndiscord.spring.interaction.slash.impl.anilist;

import fr.rakambda.rsndiscord.spring.api.anilist.AnilistService;
import fr.rakambda.rsndiscord.spring.interaction.modal.impl.AnilistTokenModal;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
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
public class AnilistRegisterCommand implements IExecutableSlashCommandGuild{
	private final AnilistService aniListService;
	
	@Autowired
	public AnilistRegisterCommand(AnilistService aniListService){
		this.aniListService = aniListService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "register";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "anilist/register";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		return JDAWrappers.reply(event, AnilistTokenModal.builder(aniListService.getCodeLink()).get()).submit();
	}
}
