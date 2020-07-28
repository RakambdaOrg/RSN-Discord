package fr.raksrinana.rsndiscord.commands.birthday;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class AddCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
	
	public AddCommand(Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		event.getMessage().getMentionedUsers().stream().findFirst()
				.ifPresent(user -> {
					args.poll();
					parseDate(event.getGuild(), args.poll()).ifPresentOrElse(date -> {
						Settings.get(event.getGuild()).getBirthdays().setDate(user, date);
						Actions.reply(event, translate(event.getGuild(), "birthday.saved"), null);
					}, () -> Actions.reply(event, translate(event.getGuild(), "birthday.bad-date"), null));
				});
		return CommandResult.SUCCESS;
	}
	
	private Optional<LocalDate> parseDate(Guild guild, String string){
		if(Objects.isNull(string)){
			return Optional.empty();
		}
		try{
			return Optional.ofNullable(LocalDate.parse(string, DF));
		}
		catch(DateTimeParseException e){
			Log.getLogger(guild).error("Failed to parse date {}", string, e);
		}
		return Optional.empty();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.birthday.add.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.birthday.add.description");
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.birthday.start.add.user"), false);
		builder.addField("date", translate(guild, "command.birthday.start.add.date", "YYYY-MM-DD"), false);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user> <date>";
	}
}
