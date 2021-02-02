package fr.raksrinana.rsndiscord.command.impl.schedule;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.guild.schedule.SimpleScheduleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command.impl.schedule.ScheduleCommandComposite.getReminderDate;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class MessageScheduleCommand extends BasicCommand{
	public MessageScheduleCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("message", translate(guild, "command.schedule.message.help.message"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.size() < 2){
			return BAD_ARGUMENTS;
		}
		return getReminderDate(args.pop()).map(date -> {
			ScheduleUtils.addScheduleAndNotify(new SimpleScheduleConfiguration(event.getAuthor(), event.getChannel(), date,
					String.join(" ", args)), event.getChannel());
			return SUCCESS;
		}).orElse(BAD_ARGUMENTS);
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> <message...>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.schedule.message", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.schedule.message.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.schedule.message.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("message");
	}
}
