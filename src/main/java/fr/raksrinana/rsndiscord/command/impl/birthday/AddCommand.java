package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Objects.isNull;

public class AddCommand extends BasicCommand{
	public AddCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.birthday.add.help.user"), false)
				.addField("date", translate(guild, "command.birthday.add.help.date", "YYYY-MM-DD"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(noUserIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		args.pop();
		var guild = event.getGuild();
		var channel = event.getChannel();
		var user = getFirstUserMentioned(event).orElseThrow();
		
		getArgumentAs(args, arg -> parseDate(guild, arg))
				.ifPresentOrElse(birthday -> {
					Settings.get(guild).getBirthdays().setBirthday(user, birthday);
					
					channel.sendMessage(translate(guild, "birthday.saved")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
				}, () -> channel.sendMessage(translate(guild, "birthday.bad-date")).submit()
						.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return SUCCESS;
	}
	
	@Nullable
	private LocalDate parseDate(@NotNull Guild guild, @Nullable String string){
		if(isNull(string)){
			return null;
		}
		try{
			return LocalDate.parse(string, ISO_LOCAL_DATE);
		}
		catch(DateTimeParseException e){
			Log.getLogger(guild).error("Failed to parse date {}", string, e);
		}
		return null;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user> <date>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.birthday.add", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.birthday.add.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.birthday.add.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add");
	}
}
