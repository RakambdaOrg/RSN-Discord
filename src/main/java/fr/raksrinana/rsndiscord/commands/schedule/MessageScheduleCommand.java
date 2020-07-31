package fr.raksrinana.rsndiscord.commands.schedule;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.guild.schedule.SimpleScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class MessageScheduleCommand extends BasicCommand{
	public MessageScheduleCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("message", translate(guild, "command.schedule.message.help.message"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.size() < 2){
			return CommandResult.BAD_ARGUMENTS;
		}
		return ScheduleCommandComposite.getReminderDate(args.pop()).map(date -> {
			ScheduleUtils.addScheduleAndNotify(new SimpleScheduleConfiguration(event.getAuthor(), event.getChannel(), date, String.join(" ", args)), event.getChannel());
			return CommandResult.SUCCESS;
		}).orElse(CommandResult.BAD_ARGUMENTS);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> <message...>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.schedule.message.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("message");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.schedule.message.description");
	}
}
