package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;

@Log4j2
public class ClearThreadCommand extends SubSlashCommand{
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		guild.retrieveActiveThreads().submit()
				.thenCompose(this::handleThreads)
				.thenCompose(empty -> event.getTextChannel().retrieveArchivedPublicThreadChannels().submit())
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
	
	@SneakyThrows
	private boolean shouldDeleteThread(@NotNull ThreadChannel threadChannel){
		return JDAWrappers.history(threadChannel)
				.takeAsync(1)
				.thenApply(messages -> messages.stream().anyMatch(this::isCorrectMessage))
				.get();
	}
	
	private boolean isCorrectMessage(@NotNull Message message){
		if(!Objects.equals(message.getAuthor().getId(), message.getJDA().getSelfUser().getId())){
			return false;
		}
		return Objects.equals(message.getContentRaw(), "Deleting this thread soon");
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
