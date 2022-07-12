package fr.raksrinana.rsndiscord.utils.jda.wrappers.interaction;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Log4j2
public class AutoCompleteWrapper extends ActionWrapper<Void, AutoCompleteCallbackAction>{
	private final ArrayList<Command.Choice> choices;
	
	public AutoCompleteWrapper(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Collection<Command.Choice> choices){
		super(event.replyChoices(choices));
		this.choices = new ArrayList<>(choices);
	}
	
	public void addChoice(@NotNull Command.Choice choice){
		choices.add(choice);
		getAction().addChoices(choice);
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Added choices {}", choices);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to add choices {}", choices, throwable);
	}
}
