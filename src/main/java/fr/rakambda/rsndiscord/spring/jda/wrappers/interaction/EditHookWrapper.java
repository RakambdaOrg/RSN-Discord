package fr.rakambda.rsndiscord.spring.jda.wrappers.interaction;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class EditHookWrapper extends ActionWrapper<Message, WebhookMessageEditAction<Message>>{
	public EditHookWrapper(@NotNull InteractionHook hook, LayoutComponent... components){
		super(hook.editOriginalComponents(components));
	}
	
	@Override
	protected void logSuccess(Message value){
		log.info("Edited components on {}", value.getId());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit components", throwable);
	}
}
