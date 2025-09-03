package fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation;

import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TagsCommand implements IExecutableSlashCommandGuild{
	public static final String CHANNEL_OPTION_ID = "channel";
	
	private final LocalizationService localizationService;
	
	@Autowired
	public TagsCommand(LocalizationService localizationService){
		this.localizationService = localizationService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "tags";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "mod/tags";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws InvalidChannelTypeException{
		var deferred = event.deferReply(true).submit();
		
		var channel = event.getOption(CHANNEL_OPTION_ID).getAsChannel().asThreadChannel();
		
		var tags = channel.getAppliedTags().stream()
				.map(t -> "%s: %s".formatted(t.getName(), t.getId()))
				.collect(Collectors.joining("\n"));
		
		var content = localizationService.translate(event.getUserLocale(), "tags.tags", tags);
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, content).submit());
	}
}
