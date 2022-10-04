package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageDeleteButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_MESSAGE;

@Log4j2
public class ClearThreadCommand extends SubSlashCommand{
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		guild.retrieveActiveThreads().submit()
				.thenCompose(this::handleThreads)
				.thenCompose(empty -> event.getGuildChannel().asThreadContainer().retrieveArchivedPublicThreadChannels().submit())
				.thenCompose(this::handleThreads);
		
		return HANDLED;
	}
	
	@NotNull
	private CompletableFuture<Void> handleThreads(@NotNull Collection<ThreadChannel> threads){
		return threads.stream()
				.filter(this::shouldDeleteThread)
				.map(JDAWrappers::delete)
				.map(ActionWrapper::submit)
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
	
	// @SneakyThrows //TODO Lombok 19
	private boolean shouldDeleteThread(@NotNull ThreadChannel threadChannel){
		try{
			return JDAWrappers.history(threadChannel)
					.takeAsync(1)
					.thenApply(messages -> messages.stream().anyMatch(this::isDeletionMessage)
					                       || (!hasParentMessage(threadChannel) && messages.stream().anyMatch(this::isCreationMessage)))
					.get();
		}
		catch(InterruptedException | ExecutionException e){
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasParentMessage(@NotNull ThreadChannel threadChannel){
		try{
			return threadChannel.retrieveParentMessage().submit()
					.thenApply(m -> true)
					.exceptionally(this::handleException)
					.get(10, TimeUnit.MINUTES);
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			log.error("Failed to get parent message", e);
			return false;
		}
	}
	
	private boolean isDeletionMessage(@NotNull Message message){
		if(!Objects.equals(message.getAuthor().getId(), message.getJDA().getSelfUser().getId())){
			return false;
		}
		return Objects.equals(message.getContentRaw(), "Deleting this thread soon");
	}
	
	private boolean isCreationMessage(@NotNull Message message){
		if(!Objects.equals(message.getAuthor().getId(), message.getJDA().getSelfUser().getId())){
			return false;
		}
		return message.getComponents().stream()
				.map(LayoutComponent::getComponents)
				.flatMap(Collection::stream)
				.filter(ActionComponent.class::isInstance)
				.map(ActionComponent.class::cast)
				.anyMatch(ac -> Objects.equals(ac.getId(), new TodoMessageDeleteButtonHandler().getId()));
	}
	
	private boolean handleException(@NotNull Throwable e){
		if(e instanceof CompletionException completionException){
			return handleException(completionException.getCause());
		}
		if(e instanceof ErrorResponseException re){
			var error = re.getErrorResponse();
			if(error == UNKNOWN_MESSAGE){
				return false;
			}
		}
		
		log.error("Got unexpected exception", e);
		return true;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "clearthread";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Clear threads that should have been deleted";
	}
}
