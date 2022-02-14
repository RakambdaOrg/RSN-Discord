package fr.raksrinana.rsndiscord.utils.jda.wrappers.interaction;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class AutoCompleteWrapper{
	private final ArrayList<Command.Choice> choices;
	private AutoCompleteCallbackAction action;
	
	public AutoCompleteWrapper(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Collection<Command.Choice> choices){
		this.choices = new ArrayList<>(choices);
		action = event.replyChoices(choices);
	}
	
	public void addChoice(@NotNull Command.Choice choice){
		action = action.addChoices(choice);
		choices.add(choice);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Added choices {}", choices));
	}
}
