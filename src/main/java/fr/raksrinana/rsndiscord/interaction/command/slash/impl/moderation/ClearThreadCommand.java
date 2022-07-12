package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@Log4j2
public class ClearThreadCommand extends SubSlashCommand{
	private static final String USER_OPTION_ID = "user";
	private static final String THREAD_COUNT_OPTION_ID = "count";
	private static final int DEFAULT_COUNT = 100;
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(USER, USER_OPTION_ID, "Channel to delete the message in (default: everyone)"),
				new OptionData(INTEGER, THREAD_COUNT_OPTION_ID, "Number of threads to delete (default " + DEFAULT_COUNT + ")")
		);
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getTextChannel();
		
		var threadCount = getOptionAsInt(event.getOption(THREAD_COUNT_OPTION_ID)).orElse(DEFAULT_COUNT);
		var predicate = Optional.ofNullable(event.getOption(USER_OPTION_ID))
				.map(OptionMapping::getAsUser)
				.map(user -> (Predicate<ThreadChannel>) tc -> Objects.equals(tc.getOwnerId(), user.getId()))
				.orElse(tc -> true);
		
		channel.retrieveArchivedPublicThreadChannels().submit()
				.thenAccept(threads -> threads.stream()
						.filter(predicate)
						.limit(threadCount)
						.map(JDAWrappers::delete)
						.forEach(ActionWrapper::submit));
		
		return HANDLED;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "clearthread";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Clear archived threads from a channel";
	}
}
