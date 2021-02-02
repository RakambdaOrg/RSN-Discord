package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.guild.schedule.UnbanScheduleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.addSchedule;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static fr.raksrinana.rsndiscord.utils.Utilities.parseDuration;

@BotCommand
public class SoftBanCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.softban.help.user"), false)
				.addField("duration", translate(guild, "command.softban.help.duration"), false)
				.addField("reason", translate(guild, "command.softban.help.reason"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty() || noMemberIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		args.pop();
		var target = getFirstMemberMentioned(event).orElseThrow();
		var duration = parseDuration(args.pop());
		var reason = String.join(" ", args);
		
		var unbanScheduleConfiguration = new UnbanScheduleConfiguration(event.getAuthor(), channel,
				ZonedDateTime.now().plus(duration), "Banned for: " + reason, target.getId());
		
		target.ban(0, reason).submit()
				.thenAccept(empty -> {
					addSchedule(guild, unbanScheduleConfiguration);
					channel.sendMessage(translate(guild, "softban.banned", target.getAsMention(), durationToString(duration), reason)).submit();
				});
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> <duration> <reason...>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.softban", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.softban.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.softban.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("softban");
	}
}
