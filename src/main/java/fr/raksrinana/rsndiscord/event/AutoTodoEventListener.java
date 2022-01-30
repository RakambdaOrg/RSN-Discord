package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageDeleteButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageKeepButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageReplyButtonHandler;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.PAPERCLIP;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.RIGHT_ARROW_CURVING_LEFT;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
@Getter
@Log4j2
public class AutoTodoEventListener extends ListenerAdapter{
	private static final ItemComponent[] buttons = {
			new TodoMessageDeleteButtonHandler().asComponent(),
			new TodoMessageKeepButtonHandler().asComponent(),
			new TodoMessageReplyButtonHandler().asComponent()
	};
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		super.onMessageReceived(event);
		if(!event.isFromGuild()){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getAuthor())){
			var guildConfiguration = Settings.get(event.getGuild());
			var message = event.getMessage();
			
			if(containsChannel(guildConfiguration.getReactionsConfiguration().getAutoTodoChannels(), event.getChannel())){
				switch(message.getType()){
					case CHANNEL_PINNED_ADD, THREAD_CREATED -> JDAWrappers.delete(message).submit();
					case DEFAULT -> handleTodo(event);
				}
			}
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
	
	private void handleTodo(@NotNull MessageReceivedEvent event){
		if(Objects.equals(event.getAuthor(), event.getJDA().getSelfUser())){
			JDAWrappers.editComponents(event.getMessage(), buttons).submit();
			return;
		}
		
		if(canForward(event)){
			forward(event);
		}
		else{
			addReactions(event);
		}
	}
	
	private boolean canForward(@NotNull MessageReceivedEvent event){
		if(event.isWebhookMessage()){
			return false;
		}
		
		var jda = event.getJDA();
		var message = event.getMessage();
		if(!message.getAttachments().isEmpty()){
			return false;
		}
		return message.getEmotes().stream()
				.map(emote -> jda.getEmoteById(emote.getId()))
				.allMatch(emote -> emote != null
				                   && emote.getGuild() != null
				                   && emote.getGuild().isMember(jda.getSelfUser())
				                   && emote.canInteract(event.getGuild().getSelfMember()));
	}
	
	private void forward(@NotNull MessageReceivedEvent event){
		var author = event.getAuthor();
		var message = event.getMessage();
		
		var forward = new MessageBuilder(message)
				.setContent("From: %s\n%s".formatted(author.getAsMention(), message.getContentRaw()))
				.build();
		var action = JDAWrappers.message(event.getChannel(), forward)
				.addActionRow(buttons);
		
		var messageReference = message.getMessageReference();
		if(Objects.nonNull(messageReference)){
			action = action.replyTo(messageReference);
		}
		
		action.submit().thenCompose(m -> JDAWrappers.delete(event.getMessage()).submit());
	}
	
	private void addReactions(@NotNull MessageReceivedEvent event){
		var guildConfiguration = Settings.get(event.getGuild());
		var message = event.getMessage();
		
		var waitingReactionMessageConfiguration = new WaitingReactionMessageConfiguration(message, TODO, Map.of(DELETE_KEY, Boolean.toString(true)));
		guildConfiguration.addMessagesAwaitingReaction(waitingReactionMessageConfiguration);
		
		JDAWrappers.addReaction(message, CHECK_OK).submit();
		JDAWrappers.addReaction(message, PAPERCLIP).submit();
		JDAWrappers.addReaction(message, RIGHT_ARROW_CURVING_LEFT).submit();
	}
}
