package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandCompletionRunner;
import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandRunner;
import fr.rakambda.rsndiscord.spring.log.LogContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SlashCommandListener extends ListenerAdapter{
	private final SlashCommandRunner slashCommandRunner;
	private final SlashCommandCompletionRunner slashCommandCompletionRunner;
	
	@Autowired
	public SlashCommandListener(SlashCommandRunner slashCommandRunner, SlashCommandCompletionRunner slashCommandCompletionRunner){
		this.slashCommandRunner = slashCommandRunner;
		this.slashCommandCompletionRunner = slashCommandCompletionRunner;
	}
	
	@Override
	public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received slash-command {} from {} with args {}", event.getFullCommandName(), event.getUser(), getArgsForLogs(event.getOptions()));
			slashCommandRunner.execute(event);
		}
	}
	
	@Override
	public void onCommandAutoCompleteInteraction(@NonNull CommandAutoCompleteInteractionEvent event){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received auto-complete {} from {} with args {}", event.getFullCommandName(), event.getUser(), event.getFocusedOption());
			slashCommandCompletionRunner.complete(event);
		}
	}
	
	@NonNull
	private String getArgsForLogs(@NonNull List<OptionMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getName(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
}
