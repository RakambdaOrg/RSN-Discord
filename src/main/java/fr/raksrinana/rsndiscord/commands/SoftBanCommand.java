package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.schedule.config.UnbanScheduleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.addSchedule;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static fr.raksrinana.rsndiscord.utils.Utilities.parseDuration;

@BotCommand
public class SoftBanCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.softban.help.user"), false)
				.addField("duration", translate(guild, "command.softban.help.duration"), false)
				.addField("reason", translate(guild, "command.softban.help.reason"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> <duration> <reason...>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.softban", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.softban.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("softban");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.softban.description");
	}
}
