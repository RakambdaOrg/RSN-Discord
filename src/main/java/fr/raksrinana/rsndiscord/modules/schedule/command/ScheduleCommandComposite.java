package fr.raksrinana.rsndiscord.modules.schedule.command;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.schedule.command.delete.DeleteCommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.parseDuration;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@BotCommand
public class ScheduleCommandComposite extends CommandComposite{
	public ScheduleCommandComposite(){
		this.addSubCommand(new MessageScheduleCommand(this));
		this.addSubCommand(new DeleteCommandComposite(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.schedule", false);
	}
	
	public static Optional<ZonedDateTime> getReminderDate(@NonNull String string){
		try{
			var duration = parseDuration(string);
			if(!duration.isZero()){
				return Optional.of(ZonedDateTime.now().plus(duration));
			}
			return Optional.of(ZonedDateTime.parse(string, ISO_DATE_TIME));
		}
		catch(DateTimeParseException ignored){
		}
		return Optional.empty();
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("delay", translate(guild, "command.schedule.help.delay"), false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.schedule.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("schedule");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.schedule.description");
	}
}
