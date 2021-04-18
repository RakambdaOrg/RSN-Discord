package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
@Slf4j
public class ListJoinsCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var limit = getArgumentAsInteger(args).orElse(50);
		var joinPos = new AtomicInteger(0);
		
		guild.loadMembers()
				.onSuccess(members -> members.stream()
						.sorted(Comparator.comparing(Member::getTimeJoined))
						.limit(limit)
						.forEachOrdered(member -> JDAWrappers.message(event, translate(guild, "list-joins.user-joined",
								joinPos.incrementAndGet(),
								member.getTimeJoined().format(DF),
								member.getAsMention()))
								.submit()))
				.onError(error -> {
					Log.getLogger(guild).error("Failed to load members", error);
					JDAWrappers.message(event, translate(guild, "list-joins.error-members")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				});
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.list-joins", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.list-joins.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.list-joins.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("listjoin");
	}
}
