package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
@Slf4j
public class ListJoinsCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.list-joins", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		var limit = getArgumentAsInteger(args).orElse(50);
		var joinPos = new AtomicInteger(0);
		
		guild.loadMembers()
				.onSuccess(members -> members.stream()
						.sorted(Comparator.comparing(Member::getTimeJoined))
						.limit(limit)
						.forEachOrdered(member -> channel
								.sendMessage(translate(guild, "list-joins.user-joined",
										joinPos.incrementAndGet(),
										member.getTimeJoined().format(DF),
										member.getAsMention()))
								.submit()))
				.onError(error -> {
					Log.getLogger(guild).error("Failed to load members", error);
					channel.sendMessage(translate(guild, "list-joins.error-members")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				});
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.list-joins.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("listjoin");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.list-joins.description");
	}
}
