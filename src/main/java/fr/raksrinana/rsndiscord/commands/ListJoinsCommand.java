package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@BotCommand
public class ListJoinsCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		int limit = Optional.ofNullable(args.poll()).filter(val -> val.chars().allMatch(Character::isDigit)).map(Integer::parseInt).orElse(50);
		final var joinPos = new AtomicInteger(0);
		event.getGuild().retrieveMembers().thenAccept(members -> event.getGuild().getMembers().stream().sorted(Comparator.comparing(Member::getTimeJoined)).limit(limit).forEachOrdered(member -> Actions.sendMessage(event.getChannel(), "#" + joinPos.incrementAndGet() + " joined at " + member.getTimeJoined().format(DF) + " is " + member.getAsMention(), null)));
		return CommandResult.SUCCESS;
	}
	
	@Override
	@NonNull
	public AccessLevel getAccessLevel(){
		return AccessLevel.CREATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "List members";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("listjoin");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "List members by their last join date";
	}
}
