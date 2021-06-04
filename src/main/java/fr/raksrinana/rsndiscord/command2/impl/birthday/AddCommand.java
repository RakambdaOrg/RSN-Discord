package fr.raksrinana.rsndiscord.command2.impl.birthday;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.util.Objects.isNull;

public class AddCommand extends SubCommand{
	private static final String USER_OPTION_ID = "user";
	private static final String DATE_OPTION_ID = "date";
	
	@Override
	@NotNull
	public String getId(){
		return "add";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Add a user's birthday";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(OptionType.USER, USER_OPTION_ID, "User").setRequired(true),
				new OptionData(OptionType.STRING, DATE_OPTION_ID, "Birthday").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var user = event.getOption(USER_OPTION_ID).getAsUser();
		var date = getOptionAs(event.getOption(DATE_OPTION_ID), v -> parseDate(guild, v));
		
		date.ifPresentOrElse(birthday -> {
			Settings.get(guild).getBirthdays().setBirthday(user, birthday);
			JDAWrappers.reply(event, translate(guild, "birthday.saved")).submit();
		}, () -> JDAWrappers.reply(event, translate(guild, "birthday.bad-date")).submit());
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
}
