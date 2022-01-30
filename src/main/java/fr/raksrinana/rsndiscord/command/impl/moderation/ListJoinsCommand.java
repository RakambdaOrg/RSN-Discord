package fr.raksrinana.rsndiscord.command.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Slf4j
public class ListJoinsCommand extends SubCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	private static final String USER_COUNT_OPTION_ID = "count";
	
	@Override
	@NotNull
	public String getId(){
		return "list-joins";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "List the fist people that joined the server";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(INTEGER, USER_COUNT_OPTION_ID, "The number of people to return (default: 50)"));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var limit = getOptionAsInt(event.getOption(USER_COUNT_OPTION_ID)).orElse(50);
		var joinPos = new AtomicInteger(0);
		
		guild.loadMembers()
				.onSuccess(members -> members.stream()
						.sorted(Comparator.comparing(Member::getTimeJoined))
						.limit(limit)
						.forEachOrdered(m -> {
							var message = translate(guild, "list-joins.user-joined",
									joinPos.incrementAndGet(),
									m.getTimeJoined().format(DF),
									m.getAsMention());
							JDAWrappers.reply(event, message).ephemeral(true).submit();
						}))
				.onError(error -> {
					log.error("Failed to load members", error);
					JDAWrappers.edit(event, translate(guild, "list-joins.error-members")).submitAndDelete(5);
				});
		return HANDLED;
	}
}
