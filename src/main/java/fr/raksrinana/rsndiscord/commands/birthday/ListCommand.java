package fr.raksrinana.rsndiscord.commands.birthday;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ListCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
	
	public ListCommand(Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Settings.get(event.getGuild()).getBirthdays().getDates()
				.forEach((key, date) -> key.getUser()
						.ifPresent(user -> Actions.reply(event, translate(event.getGuild(), "birthday.birthday", user.getAsMention(), date.format(DF), date.until(LocalDate.now()).normalized().getYears()), null)));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.birthday.list.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("list");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.birthday.list.description");
	}
}
