package fr.rakambda.rsndiscord.spring.jda.wrappers.interaction;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class AutoCompleteWrapper extends ActionWrapper<Void, AutoCompleteCallbackAction>{
	private final ArrayList<Choice> choices;
	
	public AutoCompleteWrapper(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull Collection<Choice> choices){
		super(event.replyChoices(choices));
		this.choices = new ArrayList<>(choices);
	}
	
	public void addChoice(@NonNull Choice choice){
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
